package Common.SettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsManager {

    private Document doc;
    private Element root;
    private ArrayList settings;
    private String filename;

    public SettingsManager() {
        filename = "settings.xml";
        settings = LoadSettings();
    }

    public SettingsManager(String s) {
        if (s != null) {
            filename = s + "_settings.xml";
        } else {
            filename = "settings.xml";
        }
        settings = LoadSettings();
    }

    public ArrayList LoadSettings() {

        File f = new File(System.getProperty("user.home") + System.getProperty("file.separator") + filename);

        if (f.canRead()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                System.out.println("Settings xml creation error.");
                return null;
            }
        }

        XMLParser xml = new XMLParser(new File(System.getProperty("user.home") + System.getProperty("file.separator") + filename));

        if (xml == null) {
            return null;
        }

        settings = xml.getSettings();

        return settings;
    }

    public String getSettingValue(String n) {
        if (settings == null) {
            return null;
        }
        ListIterator l = settings.listIterator();

        while (l.hasNext()) {
            SettingData s = ((SettingData) (l.next()));
            if (s.getName().equals(n)) {
                return s.getValue();
            }
        }
        return null;
    }

    public String getSettingValue(String n, String def) {
        String t = getSettingValue(n);
        if (t == null) {
            addSetting(n, def);
        }
        return (t == null ? def : t);
    }

    public String getSettingValue(String n, String def, Boolean numberRequied) {
        String t = getSettingValue(n, def);
        int i;

        if (numberRequied) {
            try {
                i = Integer.parseInt(def);
            } catch (NumberFormatException e) {
                def = "0";
            }

            try {
                i = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                t = null;
            }
        }

        if (t == null) {
            addSetting(n, def);
        }
        return t;
    }

    public void setSettingValue(String n, String v) {
        if (settings == null) {
            return;
        }
        ListIterator l = settings.listIterator();

        while (l.hasNext()) {
            SettingData s = ((SettingData) (l.next()));
            if (s.getName().equals(n)) {
                s.setValue(v);
            }
        }
    }

    private boolean saveXMLDocument(String fileName, Document doc) {
        File xmlOutputFile = new File(fileName);
        FileOutputStream fos;
        Transformer transformer = null;
        try {
            fos = new FileOutputStream(xmlOutputFile);
        } catch (FileNotFoundException e) {
            return false;
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            return false;
        }

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(fos);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            return false;
        }
        return true;
    }

    public boolean SaveSettings() {
        return SaveSettings(settings);
    }

    public void addSetting(String n, String v) {
        if (settings == null) {
            return;
        }
        ListIterator l = settings.listIterator();

        while (l.hasNext()) {
            SettingData s = ((SettingData) (l.next()));
            if (s.getName().equals(n)) {
                s.setValue(v);
                return;
            }
        }
        settings.add(new SettingData(n, v));
    }
    
    public boolean removeSetting(String n) {
        if (settings == null) {
            return false;
        }
        ListIterator l = settings.listIterator();

        while (l.hasNext()) {
            SettingData s = ((SettingData) (l.next()));
            if (s.getName().equals(n)) {
                settings.remove(s);
                return true;
            }
        }
        
        return false;
    }

    public boolean SaveSettings(ArrayList o) {

        if (o == null) {
            return false;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            return false;
        }
        doc = db.newDocument();
        root = doc.createElement("settings");
        doc.appendChild(root);

        ListIterator l = o.listIterator();

        while (l.hasNext()) {
            SettingData s = ((SettingData) (l.next()));
            Element e = doc.createElement(s.getName());
            e.setTextContent(s.getValue());
            root.appendChild(e);
        }

        if (!saveXMLDocument(System.getProperty("user.home") + System.getProperty("file.separator") + filename, doc)) {
            return false;
        }

        return true;
    }
}
