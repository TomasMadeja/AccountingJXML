package cz.pb138.accounting.db;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Class serving for representing Accounting Records for AccountingDatabase class
 *
 * @author Tomas Madeja
 */
public class AccountingRecord extends AccountingEntity implements Record {
    public final static String[] CONTACT = {"entity-telephone", "entity-email"};
    public final static String[] UNIQUE = {"entity-name", "entity-address", "entity-ico",
                                            "entity-dic", "entity-bank-information", "entity-note",
                                            "billing-date", "issuing-date", "recipient-address"};
    public final static String ITEMLIST = "item";
    public final static String[] ITEM = {"description", "quantity", "unit", "price", "name"};
    public final static String SUFFIX = "xs";

    private Element itemRoot;
    private List<Map<String, Element>> itemList;

    private Boolean expense;


    public AccountingRecord(Document doc, boolean expense) {
        super(doc, UNIQUE, CONTACT);
        itemList = new ArrayList<Map<String, Element>>();
        this.expense = expense;
        itemRoot = super.doc.createElement(ITEMLIST + SUFFIX);
        super.root.appendChild(itemRoot);
    }


    public AccountingRecord(Document doc, Node recordNode, boolean expense) {
        super(doc, recordNode, UNIQUE, CONTACT);
        itemList = new ArrayList<Map<String, Element>>();
        this.expense = expense;

        itemRoot = (Element) ((Element) recordNode).getElementsByTagName(ITEMLIST+SUFFIX).item(0);
        itemsToDict(itemRoot);
    }


    public boolean isExpense() { return expense; }


    public boolean isRevenue() { return !expense; }



    @Override
    public AccountingRecord addContact(String type, String value) {
        super.addContact(type, value);
        return this;
    }

    @Override
    public AccountingRecord removeContact(String type, String value) {
        super.removeContact(type, value);
        return this;
    }


    @Override
    public AccountingRecord changeValue(String type, String value) throws AccountingException {
        if (type.compareTo("billing-date") == 0 ||
                type.compareTo("issuing-date") == 0) {
            if (!value.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])")) {
                throw new AccountingException(ADBErrorCodes.INVALID_DATE_FORMAT,
                        "Invalid format inputting " + type + " should be YYYY-MM-DD");
            }
        }
        super.changeValue(type, value);
        return this;
    }

    @Override
    public AccountingRecord changeValue(String type, String oldValue, String newValue){
        super.changeValue(type, oldValue, newValue);
        return this;
    }

    public AccountingRecord addItem(String name, String description, String quantity, String unit, String price) {

        Element item = doc.createElement("item");
        Map<String, Element> m = new HashMap<>();

        Element e = doc.createElement("description");
        e.setTextContent(description);
        item.appendChild(e);
        m.put("description", e);

        e = doc.createElement("quantity");
        e.setTextContent(quantity);
        item.appendChild(e);
        m.put("quantity", e);

        e = doc.createElement("unit");
        e.setTextContent(unit);
        item.appendChild(e);
        m.put("unit", e);

        e = doc.createElement("price");
        e.setTextContent(price);
        item.appendChild(e);
        m.put("price", e);

        e = doc.createElement("name");
        e.setTextContent(name);
        item.appendChild(e);
        m.put("name", e);

        itemRoot.appendChild(item);
        itemList.add(m);
        return this;
    }


    public AccountingRecord editItem(String[] oldItem, String[] newItem) {
        if (oldItem.length != 5 || newItem.length != 5) {
            return this;
        }
        for (Map<String, Element> subItem : itemList) {
            if (subItem.get("name").getTextContent().compareTo(oldItem[0]) == 0 ||
                    subItem.get("description").getTextContent().compareTo(oldItem[1]) == 0 ||
                    subItem.get("quantity").getTextContent().compareTo(oldItem[2]) == 0 ||
                    subItem.get("unit").getTextContent().compareTo(oldItem[3]) == 0 ||
                    subItem.get("price").getTextContent().compareTo(oldItem[4]) == 0) {
                subItem.get("name").setTextContent(newItem[0]);
                subItem.get("description").setTextContent(newItem[1]);
                subItem.get("unit").setTextContent(newItem[2]);
                subItem.get("description").setTextContent(newItem[3]);
                subItem.get("price").setTextContent(newItem[4]);
            }
        }
        return this;
    }


    public AccountingRecord removeItem(String[] attributes) {
        if (attributes.length != 4) {
            return this;
        }
        Iterator<Map<String, Element>> iterator = itemList.iterator();
        while (iterator.hasNext()) {
            Map<String, Element> item = iterator.next();

            if (item.get("name").getTextContent().compareTo(attributes[0]) == 0 ||
                    item.get("description").getTextContent().compareTo(attributes[1]) == 0 ||
                    item.get("quantity").getTextContent().compareTo(attributes[2]) == 0 ||
                    item.get("unit").getTextContent().compareTo(attributes[3]) == 0 ||
                    item.get("price").getTextContent().compareTo(attributes[4]) == 0) {
                itemRoot.removeChild(item.get("name").getParentNode());
                iterator.remove();
                return this;
            }
        }
        return this;
    }


    public String[][] getItems() {
        String[][] items = new String[itemList.size()][5];
        int i = 0;
        for (Map<String, Element> m : itemList) {
            items[i][0] = m.get("name").getTextContent();
            items[i][1] = m.get("description").getTextContent();
            items[i][2] = m.get("unit").getTextContent();
            items[i][3] = m.get("description").getTextContent();
            items[i][4] = m.get("price").getTextContent();
        }
        return items;
    }


    private void itemsToDict(Element root) {
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
}
