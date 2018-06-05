package cz.pb138.accounting.db;

public interface Item {

    /**
     * Getter for item attribute specified by name
     * @param name attribute name
     * @return content
     */
    String value(String name);

    /**
     * Getter for name attribute
     * @return content
     */
    String name();

    /**
     * Getter for description attribute
     * @return content
     */
    String description();

    /**
     * Getter for quanitity attribute
     * @return content
     */
    String quanitity();

    /**
     * Getter for unit attribute
     * @return content
     */
    String unit();

    /**
     * Getter for price attribute
     * @return content
     */
    String price();

    /**
     * Setter for item attribute specified by name
     * @param name attribute name
     * @param value attribute value
     * @return Current object
     */
    Item changeValue(String name, String value);

    /**
     * Setter for name attribute
     * @param value attribute value
     * @return Current object
     */
    Item setName(String value);

    /**
     * Setter for decription attribute
     * @param value attribute value
     * @return Current object
     */
    Item setDescription(String value);

    /**
     * Setter for quentity attribute
     * @param value attribute value
     * @return Current object
     */
    Item setQuantity(String value);

    /**
     * Setter for unit attribute
     * @param value attribute value
     * @return Current object
     */
    Item setUnit(String value);

    /**
     * Setter for price attribute
     * @param value attribute value
     * @return Current object
     */
    Item setPrice(String value);

    /**
     * Delete this item, and delete it from list of invoiced items
     */
    void delete();
}
