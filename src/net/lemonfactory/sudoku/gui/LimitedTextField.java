package net.lemonfactory.sudoku.gui;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Custom {@link JTextField} that can limit the length of the content.
 * 
 * @author Chungmin Lee
 */
@SuppressWarnings("serial")
final class LimitedTextField extends JTextField {

    private final LimitedDocument doc;

    public LimitedTextField(int limit) {
        super();
        doc = new LimitedDocument(limit);
        setDocument(doc);
    }

    public LimitedTextField(int limit, int columns) {
        super(columns);
        doc = new LimitedDocument(limit);
        setDocument(doc);
    }

    public void setLimit(int limit) {
        doc.limit = limit;
    }

    private static class LimitedDocument extends PlainDocument {

        private int limit;

        public LimitedDocument(int limit) {
            this.limit = limit;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a)
        throws BadLocationException {
            if (getLength() + str.length() > limit)
                return;
            super.insertString(offs, str, a);
        }
    }
}
