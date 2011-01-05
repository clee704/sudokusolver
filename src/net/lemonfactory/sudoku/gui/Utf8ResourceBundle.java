package net.lemonfactory.sudoku.gui;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * ResourceBundle with UTF-8. See
 * http://www.thoughtsabout.net/blog/archives/000044.html.
 * 
 * @author Chungmin Lee
 * @version 19 Aug 2008
 */
class Utf8ResourceBundle {

    // Prevent instatiation
    private Utf8ResourceBundle() {}

    public static ResourceBundle getBundle(String baseName) {
        return filter(ResourceBundle.getBundle(baseName));
    }

    public static ResourceBundle getBundle(String baseName, Locale locale) {
        return filter(ResourceBundle.getBundle(baseName, locale));
    }

    public static ResourceBundle getBundle(
            String baseName, Locale locale, ClassLoader loader) {
        return filter(ResourceBundle.getBundle(baseName, locale, loader));
    }

    private static ResourceBundle filter(ResourceBundle bundle) {
        if (!(bundle instanceof PropertyResourceBundle))
            return bundle;
        return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
    }

    private static class Utf8PropertyResourceBundle extends ResourceBundle {

        private final PropertyResourceBundle bundle;

        Utf8PropertyResourceBundle(PropertyResourceBundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public Enumeration<String> getKeys() {
            return bundle.getKeys();
        }

        @Override
        protected Object handleGetObject(String key) {
            String value = (String) bundle.handleGetObject(key);
            if (value == null)
                return null;
            try {
                return new String(value.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 is unsupported", e);
            }
        }
    }
}
