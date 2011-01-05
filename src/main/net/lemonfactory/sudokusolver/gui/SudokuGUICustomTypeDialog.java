package net.lemonfactory.sudokusolver.gui;

import static java.awt.geom.AffineTransform.getScaleInstance;
import static java.lang.String.format;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.lemonfactory.sudokusolver.type.*;

/**
 * 
 * @author Chungmin Lee
 */
class SudokuGUICustomTypeDialog {

    private static final int MAX_BOX_WIDTH = 6;
    private static final int MAX_BOX_HEIGHT = 6;
    private static final int MAX_SIZE = 36;

    /* GUI Components */
    private final JDialog dialog;
    private final JLabel lbBoxWidth;
    private final JLabel lbBoxHeight;
    private final JLabel lbSize;
    private final JLabel lbSymbols;
    private final JLabel lbBlank;
    private final JLabel lbJigsaw;
    private final JSpinner spBoxWidth;
    private final JSpinner spBoxHeight;
    private final JSpinner spSize;
    private final JTextField tSymbols;
    private final LimitedTextField tBlank;
    private final JTextArea tJigsaw;
    private final JCheckBox cbBox;
    private final JCheckBox cbJigsaw;
    private final JCheckBox cbDiagonal;
    private final JButton bClearJigsaw;
    private final JButton bOk;
    private final JButton bCancel;
    private final JButton bHelp;

    /* Dialogs and Their Siblings */
    private final JOptionPane dHelp;
    private final JEditorPane tHelp;

    /* Etc. */
    private final JLabel lbTemp;
    private ResourceBundle messages;
    private SudokuType type;

    SudokuGUICustomTypeDialog(JFrame frame) {
        dialog = new JDialog(frame, true);
        lbTemp = new JLabel();

        dHelp = new JOptionPane();
        tHelp = new JEditorPane();
        JScrollPane scrollPane1 = new JScrollPane(tHelp);
        scrollPane1.setPreferredSize(new Dimension(300, 400));
        tHelp.setContentType("text/html; charset=UTF-8");
        scrollPane1.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        dHelp.setMessage(scrollPane1);
        dHelp.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        tHelp.setEditable(false);

        lbBoxWidth = new JLabel();
        lbBoxHeight = new JLabel();
        lbSize = new JLabel();
        lbSymbols = new JLabel();
        lbBlank = new JLabel();
        lbJigsaw = new JLabel();
        spBoxWidth = new JSpinner(new SpinnerNumberModel(3, 1, MAX_BOX_WIDTH, 1));
        spBoxHeight = new JSpinner(new SpinnerNumberModel(3, 1, MAX_BOX_HEIGHT, 1));
        spSize = new JSpinner(new SpinnerNumberModel(9, 1, MAX_SIZE, 1));
        tSymbols = new JTextField();
        tBlank = new LimitedTextField(1);
        tJigsaw = new JTextArea();
        cbBox = new JCheckBox();
        cbJigsaw = new JCheckBox();
        cbDiagonal = new JCheckBox();
        bClearJigsaw = new JButton();
        bOk = new JButton();
        bCancel = new JButton();
        bHelp = new JButton();

        addListeners();

        lbSize.setEnabled(false);
        spSize.setEnabled(false);
        tJigsaw.setEnabled(false);
        cbBox.setSelected(true);
        tJigsaw.setFont(tJigsaw.getFont().deriveFont(getScaleInstance(1.5, 1.0)));
        tJigsaw.setText(
                "111233333\n"
                 + "111222333\n"
                 + "144442223\n"
                 + "114555522\n"
                 + "444456666\n"
                 + "775555688\n"
                 + "977766668\n"
                 + "999777888\n"
                 + "999997888");
        tSymbols.setText("123456789");
        tBlank.setText(".");
        bClearJigsaw.setMargin(new Insets(0, 7, 0, 7));

        // layout
        JPanel contentPane = new JPanel();
        dialog.setLayout(new GridLayout(1, 1));
        dialog.add(contentPane);
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        JScrollPane scrollPane2 = new JScrollPane(tJigsaw);
        scrollPane2.setPreferredSize(new Dimension(200, 200));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(lbBoxWidth)
                            .addComponent(spBoxWidth))
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(lbBoxHeight)
                            .addComponent(spBoxHeight))
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(lbSize)
                            .addComponent(spSize))
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(lbSymbols)
                            .addComponent(tSymbols))
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(lbBlank)
                            .addComponent(tBlank))
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(lbJigsaw)
                            .addComponent(scrollPane2)
                            .addComponent(bClearJigsaw))
                    .addComponent(cbBox)
                    .addComponent(cbJigsaw)
                    .addComponent(cbDiagonal)
                    .addGroup(
                        layout.createParallelGroup(BASELINE)
                            .addComponent(bOk)
                            .addComponent(bCancel)
                            .addComponent(bHelp)));
        layout.setHorizontalGroup(
            layout.createParallelGroup(CENTER)
                .addGroup(
                    layout.createSequentialGroup()
                        .addGroup(
                            layout.createParallelGroup(LEADING)
                                .addComponent(lbBoxWidth)
                                .addComponent(lbBoxHeight)
                                .addComponent(lbSize)
                                .addComponent(lbSymbols)
                                .addComponent(lbBlank)
                                .addComponent(lbJigsaw))
                        .addGroup(
                            layout.createParallelGroup(LEADING)
                                .addComponent(spBoxWidth)
                                .addComponent(spBoxHeight)
                                .addComponent(spSize)
                                .addComponent(tSymbols)
                                .addComponent(tBlank)
                                .addComponent(scrollPane2))
                        .addComponent(bClearJigsaw))
                .addComponent(cbBox, LEADING)
                .addComponent(cbJigsaw, LEADING)
                .addComponent(cbDiagonal, LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(bOk)
                        .addComponent(bCancel)
                        .addComponent(bHelp)));
    }

    private void addListeners() {
        spBoxWidth.addChangeListener(new BoxMeasureChanged());
        spBoxHeight.addChangeListener(new BoxMeasureChanged());
        cbBox.addChangeListener(new BoxCheckBoxChanged());
        cbJigsaw.addChangeListener(new JigsawCheckBoxChanged());
        cbDiagonal.addChangeListener(new DiagonalCheckBoxChanged());
        bClearJigsaw.addActionListener(new ClearJigsaw());
        bOk.addActionListener(new Ok());
        bCancel.addActionListener(new Cancel());
        bHelp.addActionListener(new ShowHelpDialog());
    }

    void setTexts(ResourceBundle messages) {
        this.messages = messages;
        lbBoxWidth.setText(messages.getString("CT_BOXW"));
        lbBoxHeight.setText(messages.getString("CT_BOXH"));
        lbSize.setText(messages.getString("CT_SIZE"));
        lbSymbols.setText(messages.getString("CT_SYM"));
        lbBlank.setText(messages.getString("CT_BLNK"));
        lbJigsaw.setText(messages.getString("CT_JIGS"));
        cbBox.setText(messages.getString("CT_BOX"));
        cbJigsaw.setText(messages.getString("CT_JIGS"));
        cbDiagonal.setText(messages.getString("CT_DIAG"));
        bClearJigsaw.setText(messages.getString("CT_CLR"));
        bOk.setText(messages.getString("CT_OK"));
        bCancel.setText(messages.getString("CT_CANC"));
        bHelp.setText(messages.getString("CT_HELP"));
    }

    private class BoxMeasureChanged implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            spSize.setValue(getBoxWidth() * getBoxHeight());
        }
    }

    private class BoxCheckBoxChanged implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (cbBox.isSelected()) {
                spBoxWidth.setEnabled(true);
                spBoxHeight.setEnabled(true);
                spSize.setEnabled(false);
                spSize.setValue(getBoxWidth() * getBoxHeight());
                lbBoxWidth.setEnabled(true);
                lbBoxHeight.setEnabled(true);
                lbSize.setEnabled(false);
            } else {
                spBoxWidth.setEnabled(false);
                spBoxHeight.setEnabled(false);
                spSize.setEnabled(true);
                lbBoxWidth.setEnabled(false);
                lbBoxHeight.setEnabled(false);
                lbSize.setEnabled(true);
            }
        }
    }

    private class JigsawCheckBoxChanged implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (cbJigsaw.isSelected()) {
                tJigsaw.setEnabled(true);
                cbBox.setSelected(false);
                cbDiagonal.setSelected(false);
            } else {
                tJigsaw.setEnabled(false);
            }
        }
    }

    private class DiagonalCheckBoxChanged implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (cbDiagonal.isSelected())
                cbJigsaw.setSelected(false);
        }
    }

    private class ClearJigsaw implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            tJigsaw.setText("");
            tJigsaw.requestFocusInWindow();
        }
    }

    private class Ok implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int w = getBoxWidth();
            int h = getBoxHeight();
            int size = (Integer) spSize.getValue();
            char b = tBlank.getText().charAt(0);
            SymbolSet symbolSet;
            try {
                symbolSet = new SymbolSet(tSymbols.getText());
            } catch (Exception ex) {
                showErrorDialog(dialog, messages.getString("E_DUPSYM"));
                return;
            }
            if (size != symbolSet.size()) {
                showErrorDialog(dialog, messages.getString("E_INCONS"));
                return;
            }
            else if (symbolSet.contains(b) || symbolSet.contains(' ')) {
                showErrorDialog(dialog, format(messages.getString("E_DISALLCH"), b));
                return;
            }
            else if (b == ' ') {
                showErrorDialog(dialog, messages.getString("E_DISALLBL"));
                return;
            }
            SudokuTypeStructure structure;
            structure = RowColumnTypeStructure.getInstance(size);
            if (cbJigsaw.isSelected())
                try {
                    structure = JigsawTypeStructure.fromEncodedString(
                            structure,
                            tJigsaw.getText().replaceAll("\\s", ""),
                            '.');
                } catch (Exception ex) {
                    showErrorDialog(dialog, format(messages.getString("E_JIGSIZ"), size));
                    return;
                }
            if (cbBox.isSelected())
                structure = BoxTypeStructure.getInstance(structure, w, h);
            if (cbDiagonal.isSelected())
                structure = XTypeStructure.getInstance(structure);
            type = new SudokuType(structure, symbolSet, b);
            dialog.setVisible(false);
        }
    }

    private class Cancel implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            type = null;
            dialog.setVisible(false);
        }
    }

    private class ShowHelpDialog implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            tHelp.setText(messages.getString("CT_HELPMSG"));
            tHelp.setCaretPosition(0);
            JDialog d = dHelp.createDialog(dialog, messages.getString("CT_HELP"));
            d.setVisible(true);
        }
    }

    SudokuType prompt(SudokuType oldType) {
        dialog.setTitle(messages.getString("D_CUSTOM"));
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setMinimumSize(dialog.getPreferredSize());
        type = null;
        dialog.setVisible(true);
        return type;
    }

    private int getBoxWidth() {
        return (Integer) spBoxWidth.getValue();
    }

    private int getBoxHeight() {
        return (Integer) spBoxHeight.getValue();
    }

    private void showErrorDialog(Component parentComponent, String message) {
        lbTemp.setText(message);
        JOptionPane.showMessageDialog(
                parentComponent,
                lbTemp,
                messages.getString("D_ERROR"),
                JOptionPane.ERROR_MESSAGE);
    }
}
