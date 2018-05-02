package cz.pb138.accounting.db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingOwner {
    private Document doc;

    public final static String[] CONTACT = {"telephone", "email"};
    public final static String[] UNIQUE = {"name", "address", "ico", "dic", "bank-information", "note"};

    public Map<String, Element> uniqueElements;
    public Map<String, List<Element>> contacts;

    public AccountingOwner(Document doc) {
        this.doc = doc;
        Element root = doc.getDocumentElement();
        Element e;
        for (String u : UNIQUE) {
            e = doc.createElement(u);
            root.appendChild(e);
        }
    }

    public AccountingOwner(Document doc, Node recordNode) {
        this.doc = doc;
        uniqueElements = new HashMap<String, Element>();
        contacts = new HashMap<String, List<Element>>();

        domToDict((Element)recordNode);
    }


    private void domToDict(Element root) {
        for (String u : UNIQUE) {
            Element e = (Element) root.getElementsByTagName(u).item(0);
            uniqueElements.put(u, e);
        }

        for (String c : CONTACT) {
            if (!contacts.containsKey(c)) {
                contacts.put(c, new ArrayList<Element>());
            }
            NodeList list = root.getElementsByTagName(c);
            Element e;
            for (int i = 0; i < list.getLength(); i++) {
                e = (Element) list.item(i);
                contacts.get(e.getTagName()).add(e);
            }
        }
    }

    public void addContact(String name, String value) {
        for (String matched : CONTACT) {
            if (name.compareTo(matched) == 0) {
                Element e = doc.createElement(matched);
                e.setTextContent(value);
                doc.getDocumentElement().appendChild(e);
            }
        }
    }

    public void changeValue(String name, String value) {
        Element e;
        if (( e = uniqueElements.get(name)) != null) {
            e.setTextContent(value);
        }
    }

    public void changeValue(String name, String oldValue, String newValue){
        List l;
        if (contacts.containsKey(name)) {
            l = contacts.get(name);
            for (Object e :l) {
                if (((Element) e).getTextContent().compareTo(oldValue) == 0) {
                    ((Element) e).setTextContent(newValue);
                }
            }
        }
    }

    public String getValue(String name) {
        return uniqueElements.containsKey(name) ? uniqueElements.get(name).getTextContent() : null;
    }

    public String[] getContact(String name) {
        String[] array = null;
        if (contacts.containsKey(name)) {
            array = new String[contacts.get(name).size()];
            int i = 0;
            for (Element e : contacts.get(name)) {
                array[i] = e.getTextContent();
                i++;
            }
        }
        return array;
    }

    public void removeContact(String name, String value) {
        if (contacts.containsKey(name)) {
            for (Element e : contacts.get(name)) {
                if (e.getTextContent().compareTo(value) == 0) {
                    e.getParentNode().removeChild(
                            e
                    );
                    contacts.get(name).remove(e);
                }
            }
        }
    }
}
