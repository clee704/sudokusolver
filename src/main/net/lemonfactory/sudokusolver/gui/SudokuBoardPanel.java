package net.lemonfactory.sudokusolver.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import net.lemonfactory.sudokusolver.type.SudokuType;
import net.lemonfactory.sudokusolver.type.SudokuTypeStructure;

/**
 * 
 * @author Chungmin Lee
 */
@SuppressWarnings("serial")
final class SudokuBoardPanel extends JComponent {

    private JLabel[] cells;
    private Color[] colors;
    private Color[] bgColors;
    private char[] chars;
    private Font font;

    private SudokuType type;
    private Map<Character, Integer> symbolToIndex;
    private int boardSize;
    private int boxWidth;
    private int boxHeight;

    private int[] hor;
    private int[] ver;

    private boolean cleared;
    private boolean editable;
    private int selected;

    public SudokuBoardPanel(SudokuType t) {
        setLayout(null);
        setFocusable(true);
        addMouseListener(new MyMouseListener());
        addKeyListener(new MyKeyboardListener());
        addFocusListener(new MyFocusListener());
        sudokuTypeChanged(t);
    }

    private void sudokuTypeChanged(SudokuType t) {
        type = t;
        boxWidth = type.getStructure().boxWidth();
        boxHeight = type.getStructure().boxHeight();
        boardSize = type.getStructure().size();
        symbolToIndex = makeSymbolToIndex(type);

        removeAll();
        cells = new JLabel[boardSize * boardSize];
        colors = new Color[boardSize * boardSize];
        bgColors = new Color[boardSize * boardSize];
        chars = new char[boardSize * boardSize];

        hor = new int[boardSize + 1];
        ver = new int[boardSize + 1];

        editable = true;
        cleared = true;
        selected = -1;

        for (int i = 0; i < boardSize * boardSize; ++i) {
            add(cells[i] = new JLabel());
            cells[i].setHorizontalAlignment(SwingConstants.CENTER);
            cells[i].setVerticalAlignment(SwingConstants.CENTER);
            cells[i].setOpaque(true);
            setCellBackground(i);
        }
        repaint();
    }

    private void symbolsChanged(SudokuType t) {
        for (int i = 0; i < chars.length; ++i)
            if (chars[i] != '\0')
                chars[i] = t.getSymbols().get(symbolToIndex.get(chars[i]));
        symbolToIndex = makeSymbolToIndex(t);
        repaint();
    }

    private Map<Character, Integer> makeSymbolToIndex(SudokuType type) {
        Map<Character, Integer> symbolToIndex;
        symbolToIndex = new HashMap<Character, Integer>();
        int index = 0;
        for (Character c : type.getSymbols().toString().toCharArray())
            symbolToIndex.put(c, index++);
        return symbolToIndex;
    }

    public boolean setSudokuType(SudokuType t) {
        if (type.equals(t))
            return false;
        if (type.getStructure().equals(t.getStructure()))
            symbolsChanged(t);
        else
            sudokuTypeChanged(t);
        type = t;
        return true;
    }

    public void clear() {
        if (!cleared) {
            Arrays.fill(chars, '\0');
            Arrays.fill(colors, null);
            Arrays.fill(bgColors, null);
            cleared = true;
        }
    }

    public char getChar(int i) {
        char c = chars[i];
        return c == '\0' ? type.getBlank() : c;
    }

    public Color getColor(int i) {
        return colors[i];
    }

    public String getPuzzle() {
        StringBuilder buf = new StringBuilder(chars.length);
        for (int i = 0; i < chars.length; ++i)
            buf.append(getChar(i));
        return buf.toString();
    }

    public void setChar(int i, char c) {
        setChar(i, c, Color.BLACK);
    }

    public void setChar(int i, char c, Color color) {
        if (c == type.getBlank())
            c = '\0';
        chars[i] = c;
        colors[i] = color;
        cleared = false;
    }

    public void setEditable(boolean aFlag) {
        editable = aFlag;
        if (aFlag)
            select(selected);
        else
            unselect();
    }

    public boolean isEditable() {
        return editable;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int x;
        int y;
        int width;
        int height;
        if (d.height < d.width) {
            x = (d.width - d.height) / 2;
            y = 0;
            width = height = d.height - 1;
        } else {
            x = 0;
            y = (d.height - d.width) / 2;
            width = height = d.width - 1;
        }

        // draw outer
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);

        // draw inner lines
        ver[0] = x;
        hor[0] = y;
        ver[boardSize] = x + width - 1;
        hor[boardSize] = y + height - 1;
        for (int i = 1, j, k = 0; i < boardSize; ++i) {
            j = x + (i * (width - 1 - boxHeight) / boardSize) + 2;
            g.drawLine(j + k, y, j + k, y + height);
            ver[i] = j + k;
            if (boxWidth != 0 && i % boxWidth == 0) {
                g.drawLine(j + k + 1, y, j + k + 1, y + height);
                ++k;
            }
        }
        for (int i = 1, j, k = 0; i < boardSize; ++i) {
            j = y + (i * (height - 1 - boxWidth) / boardSize) + 2;
            g.drawLine(x, j + k, x + width, j + k);
            hor[i] = j + k;
            if (boxHeight != 0 && i % boxHeight == 0) {
                g.drawLine(x, j + k + 1, x + width, j + k + 1);
                ++k;
            }
        }

        // fix cells
        for (int i = 0, bx, by, w, h, mx, my, ind; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                mx = (boxWidth != 0 && j % boxWidth == 0 || j == 0 ? 2 : 1);
                my = (boxHeight != 0 && i % boxHeight == 0 || i == 0 ? 2 : 1);
                bx = ver[j] + mx;
                by = hor[i] + my;
                w = ver[j + 1] - ver[j] - mx;
                h = hor[i + 1] - hor[i] - my;
                ind = i * boardSize + j;
                cells[ind].setBounds(bx, by, w, h);
                cells[ind].setText(String.valueOf(chars[ind]));
            }
        }
        makeFont();
        super.paint(g);
    }

    public void repaint() {
        makeFont();
        for (int i = 0; i < cells.length; ++i) {
            cells[i].setFont(font);
            cells[i].setForeground(colors[i]);
        }
        super.repaint();
    }

    private void repaint(int index) {
        cells[index].setFont(font);
        cells[index].setForeground(colors[index]);
        super.repaint();
    }

    private void setCellBackground(int cell) {
        if (bgColors[cell] == null) {
            SudokuTypeStructure structure = type.getStructure();
            int[] cellGroupIndexes = structure.getCellGroupIndexes(cell);
            Color[] toBeMixed = new Color[cellGroupIndexes.length];
            int i = 0;
            for (int j = 0; j < cellGroupIndexes.length; ++j) {
                Color bgColor = structure.getCellGroupColor(cellGroupIndexes[j]);
                if (bgColor != null)
                    toBeMixed[i++] = bgColor;
            }
            bgColors[cell] = mix(toBeMixed, 0, i);
        }
        cells[cell].setBackground(bgColors[cell]);
    }

    // RGB mean
    private Color mix(Color[] colors, int start, int end) {
        if (start == end)
            return Color.WHITE;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = start; i < end; ++i) {
            r += colors[i].getRed();
            g += colors[i].getGreen();
            b += colors[i].getBlue();
        }
        int n = end - start;
        return new Color(r / n, g / n, b / n);
    }

    private void makeFont() {
        Dimension d = getSize();
        int height = (d.height < d.width ? d.height : d.width) - 1;
        font = new Font(
                "sans-serif",
                0,
                (int) (0.6 * (height - 1 - boxWidth) / boardSize));
    }

    private void select(int index) {
        unselect();
        selected = index;
        if (selected > -1 && editable)
            cells[selected].setBackground(cells[selected].getBackground().darker());
    }

    private void unselect() {
        if (selected > -1)
            setCellBackground(selected);
    }

    private class MyMouseListener extends MouseAdapter {

        private int calculateIndex(int x, int y) {
            int cx = -Arrays.binarySearch(ver, x) - 2;
            int cy = -Arrays.binarySearch(hor, y) - 2;
            if (cx >= 0 && cx < boardSize && cy >= 0 && cy < boardSize)
                return cy * boardSize + cx;
            return -1;
        }

        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();
            if (!editable)
                return;
            int index = calculateIndex(e.getX(), e.getY());
            switch (e.getClickCount()) {
            case 1:
                if (index != -1)
                    select(index);
                break;
            case 2:
                setChar(selected, '\0');
                repaint(selected);
                break;
            }
        }
    }

    private class MyKeyboardListener extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_ENTER:
                transferFocus();
                break;
            }
            if (!editable)
                return;
            switch (keyCode) {
            case KeyEvent.VK_LEFT:
                if (selected % boardSize != 0)
                    select(selected - 1);
                else
                    select(selected - 1 + boardSize);
                break;
            case KeyEvent.VK_RIGHT:
                if ((selected + 1) % boardSize != 0)
                    select(selected + 1);
                else
                    select(selected + 1 - boardSize);
                break;
            case KeyEvent.VK_UP:
                if (selected / boardSize != 0)
                    select(selected - boardSize);
                else
                    select(selected - boardSize + boardSize * boardSize);
                break;
            case KeyEvent.VK_DOWN:
                if (selected / boardSize != boardSize - 1)
                    select(selected + boardSize);
                else
                    select(selected + boardSize - boardSize * boardSize);
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                setChar(selected, '\0');
                repaint(selected);
                break;
            }
        }

        public void keyTyped(KeyEvent e) {
            if (!editable)
                return;
            switch (e.getModifiersEx()) {
            case 0:
            case InputEvent.SHIFT_DOWN_MASK:
                char c = e.getKeyChar();
                if (symbolToIndex.containsKey(c)) {
                    setChar(selected, c);
                    repaint(selected);
                }
                else if (c == type.getBlank()) {
                    setChar(selected, '\0');
                    repaint(selected);
                }
                break;
            }
        }
    }

    private class MyFocusListener extends FocusAdapter {

        public void focusGained(FocusEvent e) {
            select(selected > -1 ? selected : 0);
        }

        public void focusLost(FocusEvent e) {
            unselect();
        }
    }
}
