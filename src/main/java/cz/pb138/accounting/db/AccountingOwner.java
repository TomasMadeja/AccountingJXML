package cz.pb138.accounting.db;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingOwner {

    public final static String[] CONTACT = {"telephone", "email"};
    public final static String[] UNIQUE = {"name", "address", "ico", "dic", "bank-information", "note"};

    public Map<String, String> uniqueElements;
    public Map<String, List<String>> contacts;

    public Boolean expense;

    public AccountingOwner(Node recordNode, Boolean expense) {
        uniqueElements = new HashMap<String, String>();
        contacts = new HashMap<String, List<String>>();

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
    }
}
