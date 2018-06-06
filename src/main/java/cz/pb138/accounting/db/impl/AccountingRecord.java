package cz.pb138.accounting.db.impl;

import cz.pb138.accounting.db.ADBErrorCodes;
import cz.pb138.accounting.db.Item;
import cz.pb138.accounting.db.Record;
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
                                            "billing-date", "issuing-date", "recipient-address",
                                            "total-price"};
    public final static String ITEMLIST = "item";
    public final static String[] ITEM = {"description", "quantity", "unit", "price", "name"};
    public final static String SUFFIX = "xs";

    private Element itemRoot;
    private List<Item> itemList;

    private Boolean expense;


    public AccountingRecord(Document doc, boolean expense) {
        super(doc, UNIQUE, CONTACT);
        try {
            super.changeValue("billing-date", "1000-10-10");
            super.changeValue("issuing-date", "1000-10-10");
        } catch (AccountingException ex) {}
        itemList = new ArrayList<>();
        this.expense = expense;
        itemRoot = super.doc.createElement(ITEMLIST + SUFFIX);
        super.root.appendChild(itemRoot);

        try {
            super.changeValue("total-price", "0");
        } catch (AccountingException ex) {}
    }


    public AccountingRecord(Document doc, Node recordNode, boolean expense) {
        super(doc, recordNode, UNIQUE, CONTACT);
        itemList = new ArrayList<>();
        this.expense = expense;

        itemRoot = (Element) ((Element) recordNode).getElementsByTagName(ITEMLIST+SUFFIX).item(0);
        itemsToDict(itemRoot);

        try {
            super.changeValue("total-price", "0");
        } catch (AccountingException ex) {}
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
        itemList.add(new AccountingItem(name, description, quantity, unit, price));
        return this;
    }


    public Item[] getItems() {
        return itemList.toArray(new Item[0]);
    }

    public boolean isOpen() {
        return super.doc != null;
    }

    public void delete() {
        super.delete();

        itemRoot = null;
        itemList = null;
        expense = null;
    }


    private void itemsToDict(Element root) {
        NodeList list = root.getElementsByTagName(ITEMLIST);
        for (int i = 0; i < list.getLength(); i++) {
            itemList.add(new AccountingItem((Element) list.item(i)));
        }
    }

    public class AccountingItem implements Item {
        private Element item;
        private Map<String, Element> attributes;

        private final static String NAME = "name";
        private final static String DESCRIPTION = "description";
        private final static String QUANTITY = "quantity";
        private final static String UNIT = "unit";
        private final static String PRICE = "price";
        private final String[] ATTRIBUTES = {DESCRIPTION, QUANTITY, UNIT, PRICE, NAME};

        AccountingItem(Element item) {
            this.item = item;
            attributes = new HashMap<>();
            Element e;
            for (String it : ATTRIBUTES) {
                e = (Element) item.getElementsByTagName(it).item(0);
                attributes.put(it, e);
            }
            addPrice();
        }

        AccountingItem(String name, String description,
                              String quantity, String unit, String price) {
            attributes = new HashMap<>();
            this.item = doc.createElement("item");

            createAttribute(DESCRIPTION, description);
            createAttribute(QUANTITY, quantity);
            createAttribute(UNIT, unit);
            createAttribute(PRICE, price);
            createAttribute(NAME, name);

            itemRoot.appendChild(item);
            addPrice();
        }

        public String value(String name) {
            return attributes.containsKey(name) ? attributes.get(name).getTextContent() : null;
        }

        public String name() {
            return value(NAME);
        }

        public String description() {
            return value(DESCRIPTION);
        }

        public String quanitity() {
            return value(QUANTITY);
        }

        public String unit() {
            return value(UNIT);
        }

        public String price() {
            return value(PRICE);
        }

        public AccountingItem changeValue(String name, String value) {
            if (attributes.containsKey(name)) {
                if (name.compareTo(PRICE) == 0 || name.compareTo(QUANTITY) == 0) {
                    minusPrice();
                }
                attributes.get(name).setTextContent(value);
                if (name.compareTo(PRICE) == 0 || name.compareTo(QUANTITY) == 0) {
                    addPrice();
                }
            }
            return this;
        }

        public AccountingItem setName(String value) {
            return changeValue(NAME, value);
        }

        public AccountingItem setDescription(String value) {
            return changeValue(DESCRIPTION, value);
        }

        public AccountingItem setQuantity(String value) {
            return changeValue(QUANTITY, value);
        }

        public AccountingItem setUnit(String value) {
            return changeValue(UNIT, value);
        }

        public AccountingItem setPrice(String value) {
            return changeValue(PRICE, value);
        }

        public void delete() {
            minusPrice();
            itemRoot.removeChild(item);
            item = null;
            attributes = null;
        }

        private void createAttribute(String name, String value) {
            Element e = doc.createElement(name);
            e.setTextContent(value);
            item.appendChild(e);
            attributes.put(name, e);
        }

        private void minusPrice() {
            try {
                AccountingRecord.this.changeValue("total-price",
                        Double.toString(
                                Double.parseDouble(getValue("total-price"))
                                        - Double.parseDouble(this.price())*Double.parseDouble(this.quanitity())

                        ));
            } catch (AccountingException ex) {}
        }

        private void addPrice() {
            try {
                AccountingRecord.this.changeValue("total-price",
                        Double.toString(
                                Double.parseDouble(getValue("total-price"))
                                        + Double.parseDouble(this.price())*Double.parseDouble(this.quanitity())

                        ));
            } catch (AccountingException ex) {}
        }
    }
}
