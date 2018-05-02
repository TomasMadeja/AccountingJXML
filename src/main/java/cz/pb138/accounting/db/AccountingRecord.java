package cz.pb138.accounting.db;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingRecord {
    private Document doc;

    public final static String[] CONTACT = {"entity-telephone", "entity-email"};
    public final static String[] UNIQUE = {"entity-name", "entity-address", "entity-ico",
                                            "entity-dic", "entity-bank-information", "entity-note",
                                            "billing-date", "issuing-date", "recipient-address"};
    public final static String ITEMLIST = "item";
    public final static String[] ITEM = {"description", "quantity", "unit", "price"};

    public Map<String, Element> uniqueElements;
    public Map<String, List<Element>> contacts;
    public List<Map<String, Element>> itemList;

    public Boolean expense;

    public AccountingRecord(Document doc) {
        this.doc = doc;
        Element root = doc.getDocumentElement();
        Element e;
        for (String u : UNIQUE) {
            e = doc.createElement(u);
            root.appendChild(e);
        }
    }

    public AccountingRecord(Document doc, Node recordNode, Boolean expense) {
        this.doc = doc;
        uniqueElements = new HashMap<String, Element>();
        contacts = new HashMap<String, List<Element>>();
        itemList = new ArrayList<Map<String, Element>>();

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
                contacts.get(c).add(e);
            }
        }

        NodeList list = root.getElementsByTagName(ITEMLIST);
        for (int i = 0; i < list.getLength(); i++) {
            itemList.add(new HashMap<String, Element>());
            Element e;
            for (String it : ITEM) {
                e = (Element) root.getElementsByTagName(it).item(0);
                itemList.get(i).put(it, e);
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

    public void addItem(String description, String quantity, String unit, String price) {
        Element item = doc.createElement("item");
        Element e = doc.createElement("description");
        e.setTextContent(description);
        item.appendChild(e);
        e = doc.createElement("quantity");
        e.setTextContent(quantity);
        item.appendChild(e);
        e = doc.createElement("unit");
        e.setTextContent(unit);
        item.appendChild(e);
        e = doc.createElement("price");
        e.setTextContent(price);
        item.appendChild(e);
        doc.getDocumentElement().appendChild(item);
    }

}
