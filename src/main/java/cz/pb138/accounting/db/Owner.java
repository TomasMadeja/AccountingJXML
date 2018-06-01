package cz.pb138.accounting.db;

import cz.pb138.accounting.db.impl.AccountingException;

public interface Owner {

    /**
     * Adds a new contact
     * @param type contact type
     * @param value contact value
     * @return reference to the current Owner object
     */
    Owner addContact(String type, String value);

    /**
     * Removes contact specified by type and uniquely specified by value, erases all copies
     * @param type contact type
     * @param value contact value
     * @return reference to the current Owner object
     */
    Owner removeContact(String type, String value);

    /**
     * Changes value of unique attribute (specified by it's type only)
     * @param type attribute type
     * @param value new attribute value
     * @return reference to current Owner object
     * @throws AccountingException specified by ADBErrorCode
     */
    Owner changeValue(String type, String value) throws AccountingException;

    /**
     * Changes contact value, changes value of all identical contacts
     * @param type contact type
     * @param oldValue instance specifying value, old contact value
     * @param newValue new contact value
     * @return reference to current Owner object
     */
    Owner changeValue(String type, String oldValue, String newValue);

    /**
     * Getter for unigue attribute values
     * @param type attribute type
     * @return attribute value
     */
    String getValue(String type);

    /**
     * Getter for list of values of given contact type
     * @param type contact type
     * @return array of given contact values
     */
    String[] getContact(String type);
}
