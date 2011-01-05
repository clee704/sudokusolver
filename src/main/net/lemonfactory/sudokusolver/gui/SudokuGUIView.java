package net.lemonfactory.sudokusolver.gui;

import static java.lang.String.format;
import static javax.swing.JOptionPane.CANCEL_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static net.lemonfactory.sudokusolver.SudokuTypes.PRESET_TYPES;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import net.lemonfactory.sudokusolver.SudokuTypes;
import net.lemonfactory.sudokusolver.type.SudokuType;
import edu.stanford.ejalbert.BrowserLauncher;

/**
 * GUI application. Creating an instance of this class will result in
 * showing the GUI frame.
 * 
 * @author Chungmin Lee
 */
public final class SudokuGUIView {

    private static final String MESSAGE_NAME;
    private static final LocaleWithName[] AVAILABLE_LOCALES;
    private static final String HOMEPAGE_URL;

    private static final Dimension MINIMUM_SIZE;
    private static final Dimension[] DEFAULT_SIZE;

    private static final Color SOLVED = Color.BLUE.darker();
    private static final Color DIFFERENT = Color.RED.darker();

    static {
        MESSAGE_NAME = "net.lemonfactory.sudokusolver.gui.lang.messages";
        AVAILABLE_LOCALES = new LocaleWithName[] {
                new LocaleWithName(new Locale("en", "US")),
                new LocaleWithName(new Locale("ko", "KR"))};
        HOMEPAGE_URL = "https://github.com/clee704/sudokusolver";
        MINIMUM_SIZE = new Dimension(300, 400);
        DEFAULT_SIZE = new Dimension[] {
                new Dimension(410, 510),
                new Dimension(610, 710),
                new Dimension(810, 910),
                new Dimension(910, 1010)};
    }

    private final SudokuGUIModel model;

    /* GUI Components */
    private final JFrame frame;
    private final SudokuBoardPanel boardPanel;
    private final JMenu mFile; 
    private final JMenu mEdit;
    private final JMenu mType;
    private final JMenu mLanguage;
    private final JMenu mHelp;
    private final JMenuItem miSaveImg;
    private final JMenuItem miExit;
    private final JMenuItem miSolve;
    private final JMenuItem miSolveTwice;
    private final JMenuItem miSolveForAnother;
    private final JMenuItem miRevert;
    private final JMenuItem miClear;
    private final JMenuItem miInput;
    private final JMenuItem miCopy;
    private final JMenuItem[] miType;
    private final JMenuItem miCustom;
    private final JMenuItem miLang;
    private final JMenuItem miAbout;
    private final JLabel lbStatusBar;

    /* Dialogs and Their Siblings */
    private final SudokuGUICustomTypeDialog dCustomType;
    private final JOptionPane dAbout;
    private final JLabel lbAbout;
    private final JFileChooser dSaveAsImageFile;
    private final JCheckBox cbWholeFrame;

    /* Etc. */
    private final MyButtonGroup TypeMenuItemGroup;
    private final JLabel lbTemp;
    private ResourceBundle messages;
    private LocaleWithName currentLocale;
    private BrowserLauncher browserLauncher;

    public SudokuGUIView() {
        messages = Utf8ResourceBundle.getBundle(MESSAGE_NAME);
        currentLocale = new LocaleWithName(Locale.getDefault());
        browserLauncher = getBrowserLauncher();

        model = new SudokuGUIModel(SudokuTypes.BOX_9, new AfterSolving());
        model.addChangeListener(new ModelStateChangeListener());
        frame = new JFrame();
        boardPanel = new SudokuBoardPanel(model.getSudokuType());
        dCustomType = new SudokuGUICustomTypeDialog(frame);
        dAbout = getAboutDialog();
        dAbout.setMessage(lbAbout = new JLabel());
        dSaveAsImageFile = new JFileChooser();
        dSaveAsImageFile.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory()
                        || f.getName().toLowerCase().endsWith(".png");
            }
            public String getDescription() {
                return messages.getString("MSG_PNG");
            };
        });
        cbWholeFrame = new JCheckBox();
        dSaveAsImageFile.setAccessory(cbWholeFrame);
        lbTemp = new JLabel();

        mFile = new JMenu();
        mType = new JMenu();
        mEdit = new JMenu();
        mLanguage = new JMenu();
        mHelp = new JMenu();
        miSaveImg = new JMenuItem();
        miExit = new JMenuItem();
        miSolve = new JMenuItem("Solve");
        miSolveTwice = new JMenuItem("SolveTwice");
        miSolveForAnother = new JMenuItem("SolveCont");
        miRevert = new JMenuItem("Revert");
        miClear = new JMenuItem("Clear");
        miInput = new JMenuItem();
        miCopy = new JMenuItem();
        TypeMenuItemGroup = new MyButtonGroup();
        miType = new JRadioButtonMenuItem[PRESET_TYPES.size()];
        miType[0] = new JRadioButtonMenuItem();
        miType[1] = new JRadioButtonMenuItem();
        miType[2] = new JRadioButtonMenuItem();
        miType[3] = new JRadioButtonMenuItem();
        miType[4] = new JRadioButtonMenuItem();
        miType[5] = new JRadioButtonMenuItem();
        miCustom = new JRadioButtonMenuItem();
        miLang = new JMenuItem();
        miAbout = new JMenuItem();
        lbStatusBar = new JLabel(" ");

        addListeners();
        setIcon();
        setTexts();

        JMenuBar menuBar = new JMenuBar();
        JPanel inner = new JPanel(new BorderLayout(2, 2));
        JPanel statusBar = new JPanel();
        inner.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.PAGE_AXIS));
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(inner, BorderLayout.CENTER);
        frame.getContentPane().add(statusBar, BorderLayout.PAGE_END);
        inner.add(boardPanel, BorderLayout.CENTER);
        statusBar.add(new JSeparator());
        statusBar.add(lbStatusBar);
        menuBar.add(mFile);
        menuBar.add(mEdit);
        menuBar.add(mType);
        menuBar.add(mLanguage);
        menuBar.add(mHelp);
        mFile.add(miSaveImg);
        mFile.addSeparator();
        mFile.add(miExit);
        mEdit.add(miSolve);
        mEdit.add(miSolveTwice);
        mEdit.add(miSolveForAnother);
        mEdit.add(miRevert);
        mEdit.add(miClear);
        mEdit.addSeparator();
        mEdit.add(miInput);
        mEdit.add(miCopy);
        for (JMenuItem typeMenuItem : miType) {
            mType.add(typeMenuItem);
            TypeMenuItemGroup.add(typeMenuItem);
        }
        mType.add(miCustom);
        TypeMenuItemGroup.add(miCustom);
        mLanguage.add(miLang);
        mHelp.add(miAbout);

        mFile.setMnemonic(KeyEvent.VK_F);
        mType.setMnemonic(KeyEvent.VK_T);
        mEdit.setMnemonic(KeyEvent.VK_E);
        mLanguage.setMnemonic(KeyEvent.VK_L);
        miSaveImg.setMnemonic(KeyEvent.VK_A);
        miExit.setMnemonic(KeyEvent.VK_X);
        miCustom.setMnemonic(KeyEvent.VK_C);
        miInput.setMnemonic(KeyEvent.VK_I);
        miCopy.setMnemonic(KeyEvent.VK_C);
        miLang.setMnemonic(KeyEvent.VK_S);
        mHelp.setMnemonic(KeyEvent.VK_H);
        miAbout.setMnemonic(KeyEvent.VK_A);
        miSolve.setMnemonic(KeyEvent.VK_S);
        miSolveTwice.setMnemonic(KeyEvent.VK_T);
        miSolveForAnother.setMnemonic(KeyEvent.VK_A);
        miRevert.setMnemonic(KeyEvent.VK_R);
        miClear.setMnemonic(KeyEvent.VK_C);
        miSolve.setAccelerator(KeyStroke.getKeyStroke("control S"));
        miSolveTwice.setAccelerator(KeyStroke.getKeyStroke("control T"));
        miSolveForAnother.setAccelerator(KeyStroke.getKeyStroke("control A"));
        miRevert.setAccelerator(KeyStroke.getKeyStroke("control R"));
        miClear.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        miInput.setAccelerator(KeyStroke.getKeyStroke("control V"));
        miCopy.setAccelerator(KeyStroke.getKeyStroke("control C"));

        model.fireStateChanged();
        lbStatusBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        miType[0].setSelected(true);
        TypeMenuItemGroup.lastSelected(0);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setMinimumSize(MINIMUM_SIZE);
        frame.setPreferredSize(getProperSize(model.getSudokuType()));
        frame.setSize(getProperSize(model.getSudokuType()));
        frame.pack();
        frame.setVisible(true);
    }

    private BrowserLauncher getBrowserLauncher() {
        try {
            return new BrowserLauncher();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JOptionPane getAboutDialog() {
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessageType(INFORMATION_MESSAGE);
        optionPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (browserLauncher != null)
                    browserLauncher.openURLinBrowser(HOMEPAGE_URL);
            }
        });
        return optionPane;
    }

    private void addListeners() {
        miSaveImg.addActionListener(new ShowSaveAsImageFileDialog());
        miExit.addActionListener(new Exit());
        for (int i = 0; i < PRESET_TYPES.size(); ++i)
            miType[i].addActionListener(new ChangeType(i, PRESET_TYPES.get(i)));
        miCustom.addActionListener(new ChangeType(PRESET_TYPES.size(), null));
        miInput.addActionListener(new SetPuzzleFromString());
        miCopy.addActionListener(new CopyPuzzle());
        miLang.addActionListener(new SelectLanguage());
        miAbout.addActionListener(new ShowAboutDialog());
        miSolve.addActionListener(new Solve());
        miSolveTwice.addActionListener(new SolveForUnique());
        miSolveForAnother.addActionListener(new SolveForAnother());
        miRevert.addActionListener(new Revert());
        miClear.addActionListener(new Clear());
        dSaveAsImageFile.addActionListener(new SaveAsImageFile());
    }

    private void setIcon() {
        ImageIcon ii = new ImageIcon(getClass().getResource("icon/icon.png"));
        Image icon = ii.getImage();
        frame.setIconImage(icon);
    }

    private void setTexts() {
        frame.setTitle(messages.getString("TITLE"));
        mFile.setText(messages.getString("M_FILE"));
        mType.setText(messages.getString("M_TYPE"));
        mEdit.setText(messages.getString("M_EDIT"));
        mLanguage.setText(messages.getString("M_LANG"));
        mHelp.setText(messages.getString("M_HELP"));
        miSaveImg.setText(messages.getString("MI_PRNTSCR"));
        miExit.setText(messages.getString("MI_EXIT"));
        miType[0].setText("9×9 (1-9)");
        miType[1].setText("16×16 (A-P)");
        miType[2].setText("25×25 (A-Y)");
        miType[3].setText("9×9 Jigsaw (1-9)");
        miType[4].setText("9×9 Sudoku-X (1-9)");
        miType[5].setText("9×9 Hypersudoku (1-9)");
        miCustom.setText(messages.getString("MI_CUSTOM"));
        miInput.setText(messages.getString("MI_INPUT"));
        miCopy.setText(messages.getString("MI_COPY"));
        miLang.setText(messages.getString("MI_SLANG"));
        miAbout.setText(messages.getString("MI_ABOUT"));
        miSolve.setText(messages.getString("MI_SOLVE"));
        miSolveTwice.setText(messages.getString("MI_SOLVTWI"));
        miSolveForAnother.setText(messages.getString("MI_SOLVANO"));
        miRevert.setText(messages.getString("MI_REVERT"));
        miClear.setText(messages.getString("MI_CLEAR"));
        dCustomType.setTexts(messages);
        lbAbout.setText(messages.getString("MSG_ABOUT"));
        cbWholeFrame.setText(messages.getString("MSG_WHOLEFRM"));
    }

    private class ModelStateChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (model.isSolving()) {
                miSolve.setEnabled(false);
                miSolveTwice.setEnabled(false);
                miSolveForAnother.setEnabled(false);
                miRevert.setEnabled(true);
                miClear.setEnabled(false);
                boardPanel.setEditable(false);
                lbStatusBar.setText(messages.getString("S_SOLVING"));
            } else if (model.hasSolved()) {
                miSolve.setEnabled(false);
                miSolveTwice.setEnabled(false);
                if (model.hasAnotherSolution())
                    miSolveForAnother.setEnabled(true);
                else
                    miSolveForAnother.setEnabled(false);
                miRevert.setEnabled(true);
                miClear.setEnabled(true);
                boardPanel.setEditable(false);
                displaySolutionInfo();
            } else {
                miSolve.setEnabled(true);
                miSolveTwice.setEnabled(true);
                miSolveForAnother.setEnabled(false);
                miRevert.setEnabled(false);
                miClear.setEnabled(true);
                boardPanel.setEditable(true);
                lbStatusBar.setText(" ");
            }
        }

        private void displaySolutionInfo() {
            StringBuilder buf = new StringBuilder();
            if (model.getSolutionNo() == 1 && !model.hasAnotherSolution())
                buf.append(messages.getString("S_SOLUNIQ"));
            else
                buf.append(format(messages.getString("S_SOL"), model.getSolutionNo()));
            buf.append(" / ");
            buf.append(model.getTime() / 1000000.0).append(' ');
            buf.append(messages.getString("S_MILLIS")).append(", ");
            buf.append(model.getGuesses()).append(' ');
            buf.append(messages.getString("S_GUESSES"));
            lbStatusBar.setText(buf.toString());
        }
    }

    private class AfterSolving implements Runnable {

        public void run() {
            if (model.hasSolved() && model.hasAnotherSolution()) {
                int n = model.getSudokuType().getStructure().getTotalCells();
                String solution = model.getSolution();
                String given = model.getGivenPuzzle();
                for (int i = 0; i < n; ++i) {
                    char onboard = boardPanel.getChar(i);
                    char sol = solution.charAt(i);
                    if (onboard == model.getSudokuType().getBlank())
                        boardPanel.setChar(i, sol, SOLVED);
                    else if (onboard != solution.charAt(i))
                        boardPanel.setChar(i, sol, DIFFERENT);
                    else if (onboard != given.charAt(i))
                        boardPanel.setChar(i, sol, SOLVED);
                }
                boardPanel.repaint();
            } else if (model.getSolutionNo() == 0) {
                showErrorDialog(messages.getString("E_INVALID"));
                boardPanel.requestFocusInWindow();
            } else if (model.getSolutionNo() != 1) {
                showErrorDialog(messages.getString("E_NOANOSOL"));
            }
        }
    }

    private class ShowSaveAsImageFileDialog implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            dSaveAsImageFile.setDialogTitle(messages.getString("D_PRNTSCR"));
            dSaveAsImageFile.showSaveDialog(frame);
        }
    }

    private class Exit implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }

    private class ChangeType implements ActionListener {

        private final int index;
        private final SudokuType type; 

        public ChangeType(int index, SudokuType type) {
            this.index = index;
            this.type = type;
        }

        public void actionPerformed(ActionEvent e) {
            TypeMenuItemGroup.lastSelected(index);
            setSudokuType(type);
        }

        private void setSudokuType(SudokuType type) {
            if (type == null) {
                if ((type = getCustomSudokuType()) == null) {
                    TypeMenuItemGroup.revert();
                    return;
                }
            }
            model.setSudokuType(type);
            if (model.isSudokuTypeStructureChanged())
                lbStatusBar.setText(" ");
            if (model.isSudokuTypeChanged()) {
                boardPanel.setSudokuType(type);
                frame.setPreferredSize(getProperSize(type));
                frame.setSize(getProperSize(type));
            } else {
                TypeMenuItemGroup.revert();
            }
        }

        private SudokuType getCustomSudokuType() {
            if (model.isSolving())
                return null;
            return dCustomType.prompt(model.getSudokuType());
        }
    }

    private class SetPuzzleFromString implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (model.isSolving())
                return;
            SudokuType type = model.getSudokuType();
            String puzzle = boardPanel.getPuzzle();
            String symbols = type.getSymbols().toString();
            char blank = type.getBlank();
            for (;;) {
                lbTemp.setText(format(messages.getString("MSG_INPUT"), symbols, blank));
                puzzle = (String) showInputDialog(
                        frame,
                        lbTemp,
                        messages.getString("D_INPUT"),
                        PLAIN_MESSAGE,
                        null,
                        null,
                        puzzle);
                if (puzzle == null || puzzle.length() == 0)
                    break;
                puzzle = puzzle.replaceAll("\\s", "");
                if (puzzle.length() > type.getStructure().getTotalCells()) {
                    showErrorDialog(messages.getString("E_TOOLONG"));
                } else if (type.isValidPuzzle(lengthen(puzzle))) {
                    revert();
                    for (int i = 0, len = puzzle.length(); i < len; ++i)
                        boardPanel.setChar(i, puzzle.charAt(i));
                    boardPanel.repaint();
                    break;
                } else {
                    showErrorDialog(messages.getString("E_INVALID"));
                }
            }
        }

        private String lengthen(String s) {
            int length = model.getSudokuType().getStructure().getTotalCells();
            int n = length - s.length();
            if (n <= 0)
                return s;
            StringBuilder buf = new StringBuilder(s);
            for (int i = 0; i < n; ++i)
                buf.append(model.getSudokuType().getBlank());
            return buf.toString();
        }
    }

    private class CopyPuzzle implements ActionListener {

        private final JTextComponent copyHelper = new JTextArea();

        public void actionPerformed(ActionEvent e) {
            StringBuilder buf = new StringBuilder();
            buf.append(boardPanel.getPuzzle());
            int size = model.getSudokuType().getStructure().size();
            for (int i = size - 1; i > 0; --i)
                buf.insert(i * size, '\n');
            copyHelper.setText(buf.toString());
            copyHelper.setSelectionStart(0);
            copyHelper.setSelectionEnd(copyHelper.getText().length());
            copyHelper.copy();
            lbTemp.setText(messages.getString("MSG_COPY"));
            showMessageDialog(
                    frame,
                    lbTemp,
                    messages.getString("D_COPY"),
                    INFORMATION_MESSAGE);
        }
    }

    private class SelectLanguage implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            LocaleWithName selected = (LocaleWithName) showInputDialog(
                    frame,
                    "",
                    messages.getString("D_SLANG"),
                    PLAIN_MESSAGE,
                    null,
                    AVAILABLE_LOCALES,
                    currentLocale);
            if (selected != null) {
                Locale.setDefault((currentLocale = selected).getLocale());
                messages = Utf8ResourceBundle.getBundle(MESSAGE_NAME);
                setTexts();
                model.fireStateChanged();
            }
        }
    }

    private class ShowAboutDialog implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JDialog d = dAbout.createDialog(frame, messages.getString("D_ABOUT"));
            d.setVisible(true);
        }
    }

    private class Solve implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (!model.getSudokuType().isValidPuzzle(boardPanel.getPuzzle())) {
                showErrorDialog(messages.getString("E_INVALID"));
                boardPanel.requestFocusInWindow();
                return;
            }
            model.solve(boardPanel.getPuzzle());
        }
    }

    private class SolveForUnique implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            model.solveTwice(boardPanel.getPuzzle());
        }
    }

    private class SolveForAnother implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            model.solveForAnotherSolution();
        }
    }

    private class Revert implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            revert();
        }
    }

    private class Clear implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            model.reset();
            boardPanel.clear();
            boardPanel.requestFocusInWindow();
            boardPanel.repaint();
        }
    }

    private class SaveAsImageFile implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (!e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
                return;
            File f = dSaveAsImageFile.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".png"))
                f = new File(f.getAbsolutePath() + ".png");
            if (f.exists()) {
                lbTemp.setText(messages.getString("MSG_DUPFILE"));
                switch (showConfirmDialog(frame, lbTemp)) {
                case NO_OPTION:
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            dSaveAsImageFile.showSaveDialog(frame);
                        }
                    });
                    return;
                case CANCEL_OPTION:
                    return;
                }
            }
            try {
                BufferedImage image;
                if (cbWholeFrame.isSelected()) {
                    Rectangle rect = frame.getBounds();
                    image = new Robot().createScreenCapture(rect);
                    frame.paint(image.getGraphics());
                }
                else {
                    int w = boardPanel.getWidth();
                    int h = boardPanel.getHeight();
                    image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
                    boardPanel.paint(image.getGraphics());
                    if (w > h)
                        image = image.getSubimage((w - h) / 2, 0, h, h);
                    else if (w < h)
                        image = image.getSubimage(0, (h - w) / 2, w, w);
                }
                ImageIO.write(image, "png", f);
            } catch (Exception ex) {
                showErrorDialog(messages.getString("E_SAVEFILE"));
            }
        }
    }

    private void revert() {
        if (model.isSolving())
            model.abort();
        model.reset();
        String puzzle = model.getGivenPuzzle();
        if (puzzle == null)
            boardPanel.clear();
        else
            for (int i = 0; i < puzzle.length(); ++i)
                boardPanel.setChar(i, puzzle.charAt(i));
        boardPanel.requestFocusInWindow();
        boardPanel.repaint();
    }

    private Dimension getProperSize(SudokuType type) {
        int p = type.getStructure().size();
        if (p <= 12)
            return DEFAULT_SIZE[0];
        else if (p <= 20)
            return DEFAULT_SIZE[1];
        else if (p <= 30)
            return DEFAULT_SIZE[2];
        else
            return DEFAULT_SIZE[3];
    }
    private void showErrorDialog(String message) {
        lbTemp.setText(message);
        showMessageDialog(
                frame,
                lbTemp,
                messages.getString("D_ERROR"),
                ERROR_MESSAGE);
    }

    private static class MyButtonGroup extends ButtonGroup {

        private static final long serialVersionUID = 1L;

        private int last = -1;
        private int curr = -1;

        public void lastSelected(int index) {
            if (index < 0 || index > buttons.size())
                throw new ArrayIndexOutOfBoundsException();
            last = curr;
            curr = index;
        }

        public void revert() {
            if (last != -1) {
                setSelected(buttons.get(curr = last).getModel(), true);
                last = -1;
            }
        }
    }

    private static class LocaleWithName {

        private final Locale locale;
        private final String name;

        public LocaleWithName(Locale locale) {
            this.locale = locale;
            this.name = locale.getDisplayLanguage(locale);
        }

        public Locale getLocale() {
            return locale;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof LocaleWithName))
                return false;
            LocaleWithName l = (LocaleWithName) o;
            return locale.equals(l.locale) && name.equals(l.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
