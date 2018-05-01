package cz.pb138.accounting.db;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingRecord {

    public final static String[] CONTACT = {"entity-telephone", "entity-email"};
    public final static String[] UNIQUE = {"entity-name", "entity-address", "entity-ico",
                                            "entity-dic", "entity-bank-information", "entity-note",
                                            "billing-date", "issuing-date", "recipient-address"};
    public final static String ITEMLIST = "item";
    public final static String[] ITEM = {"description", "quanity", "unit", "price"};

    public Map<String, String> uniqueElements;
    public Map<String, List<String>> contacts;
    public List<Map<String, String>> itemList;

    public Boolean expense;

    public AccountingRecord(Node recordNode, Boolean expense) {
        uniqueElements = new HashMap<String, String>();
        contacts = new HashMap<String, List<String>>();
        itemList = new ArrayList<Map<String, String>>();

        this.expense = expense;

        domToDict((Element)recordNode);
    }


    private void domToDict(Element root) {
        for (String u : UNIQUE) {
            Element e = (Element) root.getElementsByTagName(u).item(0);
            uniqueElements.put(u, e.getTextContent());
        }

        for (String c : CONTACT) {
            if (!contacts.containsKey(c)) {
                contacts.put(c, new ArrayList<String>());
            }
            NodeList list = root.getElementsByTagName(c);
            Element e;
            for (int i = 0; i < list.getLength(); i++) {
                e = (Element) list.item(i);
                contacts.get(e.getTagName()).add(e.getTextContent());
            }
        }

        NodeList list = root.getElementsByTagName(ITEMLIST);
        for (int i = 0; i < list.getLength(); i++) {
            itemList.add(new HashMap<String, String>());
            Element e;
            for (String it : ITEM) {
                e = (Element) root.getElementsByTagName(it).item(0);
                itemList.get(i).put(it, e.getTextContent());
            }
        }
    }

}
