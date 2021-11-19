package dist;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.SwingUtilities;


public class SyntaxColorizer extends DocumentFilter {
    private JTextPane textPane;
    private StyledDocument styledDocument;
    private Theme theme;
    private Pattern specialPattern;
    private Pattern builtinPattern;
    private Pattern typePattern;
    private Pattern errorPattern;
    private Pattern warningPattern;

    public SyntaxColorizer(JTextPane textPane, Theme theme) {
        this.textPane = textPane;
        this.theme = theme;
        styledDocument = textPane.getStyledDocument();
        buildPatterns();
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributeSet)
            throws BadLocationException {
        super.insertString(fb, offset, text, attributeSet);

        handleTextChanged();
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);

        handleTextChanged();
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet)
            throws BadLocationException {
        super.replace(fb, offset, length, text, attributeSet);

        handleTextChanged();
    }

    private void handleTextChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateTextStyles();
            }
        });
    }

    private void buildPatterns() {
        specialPattern = Pattern.compile(theme.specialKeywords);
        builtinPattern = Pattern.compile(theme.builtinKeywords);
        typePattern = Pattern.compile(theme.typeKeywords);
        errorPattern = Pattern.compile(theme.errorKeywords);
        warningPattern = Pattern.compile(theme.warningKeywords);
    }

    public void updateTextStyles() {
        styledDocument.setCharacterAttributes(0, textPane.getText().length(), theme.defaultAttrs, true);
        String text = textPane.getText();

        var matcher = typePattern.matcher(text);
        while (matcher.find()) {
            styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), theme.typeAttrs,
                    false);
        }
        matcher = builtinPattern.matcher(text);
        while (matcher.find()) {
            styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), theme.builtinAttrs,
                    false);
        }

        matcher = specialPattern.matcher(text);
        while (matcher.find()) {
            styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), theme.specialAttrs,
                    false);
        }
        matcher = errorPattern.matcher(text);
        while (matcher.find()) {
            styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), theme.errorAttrs,
                    false);
        }
        matcher = warningPattern.matcher(text);
        while (matcher.find()) {
            styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), theme.warningAttrs,
                    false);
        }
    }

}
