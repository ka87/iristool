package Common.SettingsManager;

import java.io.*;
import java.util.ArrayList;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class XMLParser {

    private Document doc;

    public XMLParser(String xml) {
        doc = parseXml(xml);
    }

    public XMLParser(File xml) {

        doc = parseXml(xml);
    }

    public ArrayList getSettings() {
        ArrayList l = new ArrayList();
        int i;
        if (doc == null) {
            return l;
        }
        Node root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (i = 0; i < children.getLength(); i++) {
            if ("#text".equals(children.item(i).getNodeName())) {
                continue;
            }

            l.add(new SettingData(children.item(i).getNodeName(), children.item(i).getTextContent()));
        }

        return l;
    }

    private Document parseXml(String xml) {
        DocumentBuilder docBuilder;
        Document d = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("XML: wrong parser configuration: " + e.getMessage());
            return null;
        }
        try {

            d = docBuilder.parse(new InputSource(new StringReader(xml)));
        } catch (IOException ex) {
            System.out.println("XML: io error upon xml reading: " + ex.getMessage());
        } catch (SAXException e) {
            System.out.println("XML: wrong xml file: " + e.getMessage());
            return null;
        }
        return d;
    }

    private Document parseXml(File xml) {
        DocumentBuilder docBuilder;
        Document d = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("XML: wrong prser configuration: " + e.getMessage());
            return null;
        }
        try {

            d = docBuilder.parse(xml);
        } catch (IOException ex) {
            System.out.println("XML: io error upon xml reading: " + ex.getMessage());
        } catch (SAXException e) {
            System.out.println("XML: wrong xml file: " + e.getMessage());
            return null;
        }
        return d;
    }
}
