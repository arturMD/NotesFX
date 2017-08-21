package artur.md.notesfx;

import javafx.scene.control.Alert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

class Settings {
    private final String settingsFile;
    private String fontColor;
    private String bgColor;

    Settings() {
        settingsFile = "settings.xml";
        load();
    }

    Settings(String fileName) {
       settingsFile = fileName;
       load();
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void load() {
        File file = new File(settingsFile);
        if(!file.exists()) {
            fontColor = "#ffffff";
            bgColor = "#000000";
            save();
        } else {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("note");
                for (int i = 0; i < nList.getLength(); i++) {
                    Node node = nList.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) node;
                        fontColor = elem.getAttribute("color");
                        bgColor = elem.getAttribute("bgColor");
                    }
                }
            } catch(Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText(null);
                alert.setContentText("Can't load settings");
                alert.showAndWait();
            }

        }
    }

    public void save() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);

            Element noteElement = doc.createElement("note");
            Attr colorAttr = doc.createAttribute("color");
            colorAttr.setValue(fontColor);
            noteElement.setAttributeNode(colorAttr);
            Attr bgColorAttr = doc.createAttribute("bgColor");
            bgColorAttr.setValue(bgColor);
            noteElement.setAttributeNode(bgColorAttr);
            rootElement.appendChild(noteElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(settingsFile));
            transformer.transform(source,result);

        } catch(Exception ex) {
            Alert alert =new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Unable to save settings");
            alert.showAndWait();
        }
    }
}
