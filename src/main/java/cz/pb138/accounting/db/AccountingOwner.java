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

    public Boolean expense;

    public AccountingOwner(Document doc) {
        this.doc = doc;
        Element root = doc.getDocumentElement();
        Element e;
        for (String u : UNIQUE) {
            e = doc.createElement(u);
            root.appendChild(e);
        }
    }

    public AccountingOwner(Document doc, Node recordNode, Boolean expense) {
        this.doc = doc;
        uniqueElements = new HashMap<String, Element>();
        contacts = new HashMap<String, List<Element>>();

        this.expense = expense;

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
}
