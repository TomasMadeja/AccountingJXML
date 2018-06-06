package cz.pb138.accounting.fn;

import cz.pb138.accounting.gui.ContactTable;
import cz.pb138.accounting.gui.ItemTable;
import javafx.collections.ObservableList;

public interface AccountingFn {

    /**
     * Kill database.
     */
    void killDatabase();

    /**
     * Match regex for input.
     *
     * @param arg  the arg
     * @param type the type
     * @return the string
     */
    String matchPoint(String arg, InputType type);

    /**
     * Match regex for contact.
     *
     * @param arg  the arg
     * @param type the type
     * @return the string
     */
    String matchPoint(String arg, ContactType type);

    /**
     * Match regex for items.
     *
     * @param arg  the arg
     * @param type the type
     * @return the string
     */
    String matchPoint(String arg, ItemsType type);

    /**
     * Match regex for date.
     *
     * @param arg  the arg
     * @param date the date
     * @return the string
     */
    String matchPoint(String arg, DateType date);

    /**
     * Gets owner.
     *
     * @param arg the arg
     * @return the owner
     */
    String getOwner(String arg);

    /**
     * Get owner contact string [ ].
     *
     * @param arg the arg
     * @return the string [ ]
     */
    String[] getOwnerContact(String arg);

    /**
     * Update name boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    boolean updateName(String arg);

    /**
     * Update address boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    boolean updateAddress(String arg);

    /**
     * Update ico boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    boolean updateICO(String arg);

    /**
     * Update dic boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    boolean updateDIC(String arg);

    /**
     * Update bank boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    boolean updateBank(String arg);

    /**
     * Update note boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    boolean updateNote(String arg);

    /**
     * Update contacts boolean.
     *
     * @param list    the list
     * @param delList the del list
     * @return the boolean
     */
    boolean updateContacts(ObservableList<ContactTable> list,
                                  ObservableList<ContactTable> delList);


    /**
     * Save invoice.
     * @param name string
     * @param address string
     * @param ico string
     * @param dic string
     * @param bank string
     * @param note string
     * @param isPayer type
     * @param contacts contacts
     * @param items items
     * @param recipient address
     * @param issuingDate date
     * @param billingDate date
     * @return bool
     */
    boolean saveInvoice(
            String name,
            String address,
            String ico,
            String dic,
            String bank,
            String note,
            Boolean isPayer,
            ObservableList<ContactTable> contacts,
            ObservableList<ItemTable> items,
            String recipient,
            String issuingDate,
            String billingDate
    );

    /**
     * Create PDF invoices
     * @param out string
     * @return true if successful, else false
     */
    boolean getPDF(String out);

    /**
     * Get value sum.
     * @param after date
     * @param before date
     * @return string
     */
    String summarizeMoney(String after, String before);
}
