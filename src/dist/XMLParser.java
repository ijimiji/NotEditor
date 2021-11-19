package dist;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.xpath.*;


public class XMLParser {
    private Document doc;
    private XPath xpath;

    public String get(String path) {
        String result = null;
        try {
            var expression = xpath.compile(path + "/text()");
            result = (String) expression.evaluate(doc, XPathConstants.STRING);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    public ArrayList<String> getMany(String path) {
        var result = new ArrayList<String>();
        try {
            var expression = xpath.compile(path);
            NodeList nodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
            for (var i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                result.add(node.getNodeValue());
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    public XMLParser(String fString) {
        try {
            File stocks = new File(fString);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            XPathFactory xpathFactory = XPathFactory.newInstance();
            this.doc = dBuilder.parse(stocks);
            this.xpath = xpathFactory.newXPath();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
