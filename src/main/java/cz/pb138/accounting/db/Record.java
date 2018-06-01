package cz.pb138.accounting.db;

import cz.pb138.accounting.db.impl.AccountingException;

public interface Record {

    /**
     * Checks if record is expense
     * @return True if expense, else false
     */
    boolean isExpense();

    /**
     * Checks if record is revenue
     * @return True if revenue, else false
     */
    boolean isRevenue();

    /**
     * Adds a new contact
     * @param type contact type
     * @param value contact value
     * @return reference to the current Record object
     */
    Record addContact(String type, String value);

    /**
     * Removes contact specified by type and uniquely specified by value, erases all copies
     * @param type contact type
     * @param value contact value
     * @return reference to the current Record object
     */
    Record removeContact(String type, String value);

    /**
     * Changes value of unique attribute (specified by it's type only)
     * @param type attribute type
     * @param value new attribute value
     * @return reference to current Record object
     * @throws AccountingException specified by ADBErrorCode
     */
    Record changeValue(String type, String value) throws AccountingException;

    /**
     * Changes contact value, changes value of all identical contacts
     * @param type contact type
     * @param oldValue instance specifying value, old contact value
     * @param newValue new contact value
     * @return reference to current Record object
     */
    Record changeValue(String type, String oldValue, String newValue);

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

    /**
     * Adds new item with values specified by parameters
     * @param name item name
     * @param description item description
     * @param quantity item quantity
     * @param unit item unit
     * @param price item total price
     * @return reference to the current Record object
     */
    Record addItem(String name, String description, String quantity, String unit, String price);

    /**
     * Edit item in item list
     * @param oldItem old item to be edited [description, quanity, unit, value]
     * @param newItem new values of the item [description, quanity, unit, value]
     * @return reference to the current Record object
     */
    Record editItem(String[] oldItem, String[] newItem);

    /**
     * Removes first occurance of the object
     * @param attributes item attributes in order of [name, description, quantity, unit, price]
     * @return reference to the current Record object
     */
    Record removeItem(String[] attributes);

    /**
     * Tranforms all items into array of arays of attributes
     * @return array of attributes arrays in order of [name, description, quantity, unit, price]
     */
    String[][] getItems();

    /**
     * Deletes the record from database
     */
    void delete();
}
