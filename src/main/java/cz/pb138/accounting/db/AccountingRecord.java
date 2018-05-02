package cz.pb138.accounting.db;

import com.github.krukow.clj_lang.Obj;
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

    public AccountingRecord(Document doc, boolean expense) {
        this.doc = doc;
        this.expense = expense;
        Element root = doc.getDocumentElement();
        Element e;
        for (String u : UNIQUE) {
            e = doc.createElement(u);
            root.appendChild(e);
        }
    }

    public AccountingRecord(Document doc, Node recordNode, boolean expense) {
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

    public void editItem(String[] oldItem, String[] newItem) {
        if (oldItem.length != 4 || newItem.length != 4) {
            return;
        }
        for (Map<String, Element> subItem : itemList) {
            if (subItem.get("description").getTextContent().compareTo(oldItem[0]) == 0 ||
                    subItem.get("quantity").getTextContent().compareTo(oldItem[1]) == 0 ||
                    subItem.get("unit").getTextContent().compareTo(oldItem[2]) == 0 ||
                    subItem.get("price").getTextContent().compareTo(oldItem[3]) == 0) {
                subItem.get("description").setTextContent(newItem[0]);
                subItem.get("unit").setTextContent(newItem[1]);
                subItem.get("description").setTextContent(newItem[2]);
                subItem.get("price").setTextContent(newItem[3]);
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

    public String[][] getItems() {
        String[][] items = new String[itemList.size()][4];
        int i = 0;
        for (Map<String, Element> m : itemList) {
            items[i][0] = m.get("description").getTextContent();
            items[i][1] = m.get("unit").getTextContent();
            items[i][2] = m.get("description").getTextContent();
            items[i][3] = m.get("price").getTextContent();
        }
        return items;
    }

    public void removeItem(String[] item) {
        if (item.length != 4) {
            return;
        }
        for (Map<String, Element> subItem : itemList) {
            if (subItem.get("description").getTextContent().compareTo(item[0]) == 0 ||
                    subItem.get("quantity").getTextContent().compareTo(item[1]) == 0 ||
                    subItem.get("unit").getTextContent().compareTo(item[2]) == 0 ||
                    subItem.get("price").getTextContent().compareTo(item[3]) == 0) {
                subItem.get("decription").getParentNode().getParentNode().removeChild(
                        subItem.get("description").getParentNode()
                );
                itemList.remove(subItem);
            }
        }
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
