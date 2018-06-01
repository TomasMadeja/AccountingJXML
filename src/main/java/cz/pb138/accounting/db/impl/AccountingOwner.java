package cz.pb138.accounting.db.impl;

import cz.pb138.accounting.db.Owner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class representing owner document (as a record)
 *
 * @author Tomas Madeja
 */
public class AccountingOwner extends AccountingEntity implements Owner {
    public final static String[] CONTACT = {"telephone", "email"};
    public final static String[] UNIQUE = {"name", "address", "ico", "dic", "bank-information", "note"};


    public AccountingOwner(Document doc) {
        super(doc, UNIQUE, CONTACT);
    }


    public AccountingOwner(Document doc, Node rootNode) {
        super(doc, ((Element )rootNode).getElementsByTagName("record").item(0), UNIQUE, CONTACT);
    }


    @Override
    public AccountingOwner addContact(String type, String value) {
        super.addContact(type, value);
        return this;
    }

    @Override
    public AccountingOwner removeContact(String type, String value) {
        super.removeContact(type, value);
        return this;
    }

    @Override
    public AccountingOwner changeValue(String type, String value) throws AccountingException {
        super.changeValue(type, value);
        return this;
    }

    @Override
    public AccountingOwner changeValue(String type, String oldValue, String newValue){
        super.changeValue(type, oldValue, newValue);
        return this;
    }
}
