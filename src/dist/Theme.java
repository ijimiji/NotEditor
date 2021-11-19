package dist;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.text.*;


public class Theme {
    final StyleContext cont = StyleContext.getDefaultStyleContext();
    String keywordRegex(ArrayList<String> keywords){
        // Example:
        // "(\\W)*(private|public|protected)"
        String regex = "(";
        for (var i = 0; i < keywords.size(); i++){
            regex += keywords.get(i);
            if (i != keywords.size() - 1){
                regex += "|";
            }
        }
        regex += ")";
        return regex;
    }
    public String fontFamily = "Monospace";
    public Integer fontSize = 22;
    public Color  black           = Color.decode("#000000");
    public Color  white           = Color.decode("#ffffff");;
    public Color  red             = Color.decode("#ff000");
    public Color  green           = Color.decode("#00ff00");
    public Color  blue            = Color.decode("#0000ff");
    public Color  yellow          = Color.decode("#ffffff");
    public Color  gray            = Color.decode("#ffffff");
    public Color  orange          = Color.decode("#ffffff");
    public Color  purple          = Color.decode("#ffffff");
    public String warningKeywords = keywordRegex(new ArrayList<String>());
    public String typeKeywords    = keywordRegex(new ArrayList<String>());
    public String errorKeywords   = keywordRegex(new ArrayList<String>());
    public String builtinKeywords = keywordRegex(new ArrayList<String>());
    public String specialKeywords = keywordRegex(new ArrayList<String>());

    public AttributeSet defaultAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);
    public AttributeSet warningAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);
    public AttributeSet typeAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);
    public AttributeSet errorAttrs   = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);
    public AttributeSet builtinAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);
    public AttributeSet specialAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);

    public Theme(){
        try {
            var xml = new XMLParser("theme.xml");

            this.fontFamily = xml.get("/theme/font/family");
            this.fontSize = Integer.parseInt(xml.get("/theme/font/size"));

            this.black = Color.decode(xml.get("/theme/colors/black"));
            this.white = Color.decode(xml.get("/theme/colors/white"));
            this.red = Color.decode(xml.get("/theme/colors/red"));
            this.green = Color.decode(xml.get("/theme/colors/green"));
            this.blue = Color.decode(xml.get("/theme/colors/blue"));
            this.yellow = Color.decode(xml.get("/theme/colors/yellow"));
            this.gray = Color.decode(xml.get("/theme/colors/gray"));
            this.orange = Color.decode(xml.get("/theme/colors/orange"));
            this.purple = Color.decode(xml.get("/theme/colors/purple"));
            this.warningKeywords = keywordRegex(xml.getMany("/theme/keywords/warning/list/element/text()"));
            this.typeKeywords    = keywordRegex(xml.getMany("/theme/keywords/type/list/element/text()"));
            this.builtinKeywords = keywordRegex(xml.getMany("/theme/keywords/builtin/list/element/text()"));
            this.errorKeywords = keywordRegex(xml.getMany("/theme/keywords/error/list/element/text()"));
            this.specialKeywords = keywordRegex(xml.getMany("/theme/keywords/special/list/element/text()"));

            this.defaultAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, white);
            this.specialAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, orange);
            this.warningAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, yellow);
            this.typeAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, green);
            this.errorAttrs   = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, red);
            this.builtinAttrs = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, blue);
        } catch (Exception e){
            System.out.println("File theme.xml not found. Using default colors.");
        }
    }
}
