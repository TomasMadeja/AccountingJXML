package cz.pb138.accounting.fn;

import cz.pb138.accounting.db.AccountingDatabase;
import cz.pb138.accounting.db.Record;
import cz.pb138.accounting.db.impl.AccountingDatabaseImpl;
import cz.pb138.accounting.db.impl.AccountingException;
import cz.pb138.accounting.gui.ContactTable;
import cz.pb138.accounting.gui.ItemTable;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Functionality class.
 */
public class AccountingFnImpl {

    private AccountingDatabase db;

    private Map<Integer, Pattern> regexes = new HashMap<>();

    // Set server
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";

    /**
     * Setup database.
     *
     * @throws AccountingException the accounting exception
     */
    public AccountingFnImpl() throws AccountingException {
        // Start server embedded mode
        db = new AccountingDatabaseImpl(USERNAME, PASSWORD);

        initRegexes();
    }

    /**
     * Kill database.
     */
    public void killDatabase() {
        try {
            db.killDatabase();
        } catch (AccountingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates all regex instances.
     */
    private void initRegexes() {
        regexes.put(getIntType(InputType.NAME),
                Pattern.compile("^[A-Za-z ]+$"));
        regexes.put(getIntType(InputType.ADDRESS),
                Pattern.compile("^[A-Za-z ,0-9]+$"));
        regexes.put(getIntType(InputType.ICO),
                Pattern.compile("^[0-9]+$"));
        regexes.put(getIntType(InputType.DIC),
                Pattern.compile("^[A-Z]{2}[0-9]+$"));
        regexes.put(getIntType(InputType.BANK),
                Pattern.compile("^[0-9]{0,6}[-]*[0-9]{1,10}/[0-9]{4}$"));
        regexes.put(getIntType(ContactType.EMAIL),
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+" +
                        "\\.[A-Za-z]{2,6}$"));
        regexes.put(getIntType(ContactType.TELEPHONE),
                Pattern.compile("^\\+[0-9]{12}$"));

        regexes.put(getIntType(ItemsType.NAME),
                Pattern.compile("^[A-Za-z ]+$"));
        regexes.put(getIntType(ItemsType.UNIT),
                Pattern.compile("^[a-z]+$"));
        regexes.put(getIntType(ItemsType.PRICE),
                Pattern.compile("^[0-9]+$"));
        regexes.put(getIntType(ItemsType.QUANTITY),
                Pattern.compile("^[0-9]+$"));

        regexes.put(getIntType(DateType.DATE),
                Pattern.compile("^([1-9]|1[0-2])/([1-9]|[12][0-9]|3[01])/" +
                        "\\d{4}$"));
    }

    /**
     * Get code of warnings.
     * @param input type
     * @return code
     */
    private Integer getIntType(InputType input) {
        switch (input) {
            case BANK:
                return InputType.BANK.getValue();
            case NAME:
                return InputType.NAME.getValue();
            case ICO:
                return InputType.ICO.getValue();
            case DIC:
                return InputType.DIC.getValue();
            case ADDRESS:
                return InputType.ADDRESS.getValue();
            default: return 0;
        }
    }

    private Integer getIntType(ContactType contact) {
        switch (contact) {
            case TELEPHONE:
                return ContactType.TELEPHONE.getValue();
            case EMAIL:
                return ContactType.EMAIL.getValue();
            default: return 0;
        }
    }

    private Integer getIntType(ItemsType item) {
        switch (item) {
            case NAME:
                return ItemsType.NAME.getValue();
            case UNIT:
                return ItemsType.UNIT.getValue();
            case PRICE:
                return ItemsType.PRICE.getValue();
            case QUANTITY:
                return ItemsType.QUANTITY.getValue();
            default: return 0;
        }
    }

    private Integer getIntType(DateType date) {
        switch (date) {
            case DATE:
                return DateType.DATE.getValue();
            default:
                return 0;
        }
    }

    /**
     * Match regex for input.
     *
     * @param arg  the arg
     * @param type the type
     * @return the string
     */
    public String matchPoint(String arg, InputType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        switch (type) {
            case NAME:
                return "A-Z, a-z and spaces";
            case ADDRESS:
                return "A-Z, a-z, 0-9, spaces and comma";
            case ICO:
                return "Numbers";
            case DIC:
                return "Example CZ00001111";
            case BANK:
                return "Example 00000-1111111/3333";
            default:
                return "";
        }
    }

    /**
     * Match regex for contact.
     *
     * @param arg  the arg
     * @param type the type
     * @return the string
     */
    public String matchPoint(String arg, ContactType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        switch (type) {
            case TELEPHONE:
                return "Example +420722633544";
            case EMAIL:
                return "Wrong format";
            default:
                return "";
        }
    }

    /**
     * Match regex for items.
     *
     * @param arg  the arg
     * @param type the type
     * @return the string
     */
    public String matchPoint(String arg, ItemsType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        switch (type) {
            case NAME:
                return "A-Z, a-z and spaces";
            case UNIT:
                return "Only a-z";
            case PRICE:
                return "Only numbers";
            case QUANTITY:
                return "Only numbers";
            default:
                return "";
        }
    }

    /**
     * Match regex for date.
     *
     * @param arg  the arg
     * @param date the date
     * @return the string
     */
    public String matchPoint(String arg, DateType date) {
        if (matchInputs(arg, date)) {
            return "";
        }

        switch (date) {
            case DATE:
                return "Example 12/24/1989";
            default:
                return "";
        }
    }

    /**
     * Is in match or not.
     * @param arg string
     * @param type type
     * @return bool
     */
    private boolean matchInputs(String arg, InputType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    private boolean matchInputs(String arg, ContactType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    private boolean matchInputs(String arg, ItemsType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    private boolean matchInputs(String arg, DateType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    /**
     * Gets owner.
     *
     * @param arg the arg
     * @return the owner
     */
    public String getOwner(String arg) {
        try {
            String val = db.getOwner().getValue(arg);
            if (val.length() > 0) {
                return val;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get owner contact string [ ].
     *
     * @param arg the arg
     * @return the string [ ]
     */
    public String[] getOwnerContact(String arg) {
        try {
            return db.getOwner().getContact(arg);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Common update.
     * @param arg string
     * @param select type string
     * @param input type
     * @return bool
     */
    private boolean updater(String arg, String select, InputType input) {
        if (matchInputs(arg, input)) {
            try {
                db.getOwner().changeValue(select, arg);
            } catch (AccountingException e) {
                e.printStackTrace();
            }
            return commitMe();
        }
        return false;
    }

    /**
     * Update name boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    public boolean updateName(String arg) {
        return updater(arg, "name", InputType.NAME);
    }

    /**
     * Update address boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    public boolean updateAddress(String arg) {
        return updater(arg, "address", InputType.ADDRESS);
    }

    /**
     * Update ico boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    public boolean updateICO(String arg) {
        return updater(arg, "ico", InputType.ICO);
    }

    /**
     * Update dic boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    public boolean updateDIC(String arg) {
        return updater(arg, "dic", InputType.DIC);
    }

    /**
     * Update bank boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    public boolean updateBank(String arg) {
        return updater(arg, "bank-information", InputType.BANK);
    }

    /**
     * Update note boolean.
     *
     * @param arg the arg
     * @return the boolean
     */
    public boolean updateNote(String arg) {
        try {
            db.getOwner().changeValue("note", arg);
        } catch (AccountingException e) {
            e.printStackTrace();
        }
        return commitMe();
    }

    /**
     * Update contacts boolean.
     *
     * @param list    the list
     * @param delList the del list
     * @return the boolean
     */
    public boolean updateContacts(ObservableList<ContactTable> list,
                                  ObservableList<ContactTable> delList) {
        if (delList.size() > 0) {
            for (ContactTable ele : delList) {
                db.getOwner().removeContact(ele.getType(), ele.getValue());
            }
        }

        if (list.size() > 0) {
            for (ContactTable elem : list) {
                if (!elem.getInDatabase()) {
                    if ((matchInputs(elem.getValue(), ContactType.EMAIL) &&
                            elem.getType().equals("email")) ||
                            (matchInputs(elem.getValue(), ContactType.TELEPHONE)
                                    && elem.getType().equals("telephone"))) {
                        db.getOwner().addContact(
                                elem.getType(),
                                elem.getValue()
                        );
                    }
                }
            }
        }

        return commitMe();
    }

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
    public boolean saveInvoice(
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
            ) {
        if (matchInputs(name, InputType.NAME) &&
                matchInputs(address, InputType.ADDRESS) &&
                matchInputs(ico, InputType.ICO) &&
                matchInputs(dic, InputType.DIC) &&
                matchInputs(bank, InputType.BANK) &&
                matchInputs(recipient, InputType.ADDRESS) &&
                matchInputs(issuingDate, DateType.DATE) &&
                matchInputs(billingDate, DateType.DATE)
                ) {
            Record record = db.addExpenditure();

            for (ItemTable item : items) {
                if (matchInputs(item.getNameVal(), ItemsType.NAME) &&
                        matchInputs(item.getUnit(), ItemsType.UNIT) &&
                        matchInputs(item.getQuantity(), ItemsType.QUANTITY) &&
                        matchInputs(item.getPrice(), ItemsType.PRICE)) {

                    String price = item.getPrice();
                    if (!isPayer) {
                        price = "-" + price;
                    }

                    record.addItem(
                            item.getNameVal(),
                            item.getDesc(),
                            item.getQuantity(),
                            item.getUnit(),
                            price);
                } else {
                    record.delete();
                    return false;
                }
            }

            for (ContactTable contact : contacts) {
                if (
                        (contact.getType().equals("email") &&
                        matchInputs(contact.getValue(), ContactType.EMAIL)) ||
                        (contact.getType().equals("telephone") &&
                        matchInputs(contact.getValue(), ContactType.TELEPHONE))
                        ) {
                    record.addContact("entity-" + contact.getType(),
                            contact.getValue());
                } else {
                    record.delete();
                    return false;
                }
            }

            try {
                record.changeValue("entity-name", name);
                record.changeValue("entity-address", address);
                record.changeValue("entity-ico", ico);
                record.changeValue("entity-dic", dic);
                record.changeValue("entity-bank-information", bank);
                record.changeValue("entity-note", note);
                record.changeValue("recipient-address", recipient);

                String[] billing = billingDate.split("/");
                String[] issuing = issuingDate.split("/");

                record.changeValue("issuing-date", issuing[2] +
                        "-" + expandDate(issuing[0]) + "-" +
                        expandDate(issuing[1]));
                record.changeValue("billing-date", billing[2] +
                        "-" + expandDate(billing[0]) + "-" +
                        expandDate(billing[1]));

                commitMe();
                return true;
            } catch (AccountingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String expandDate(String arg) {
        if (arg.length() == 1) {
            return "0" + arg;
        }
        return arg;
    }

    /**
     * Commit changes.
     * @return bool
     */
    private boolean commitMe() {
        try {
            db.commitChanges();
            return true;
        } catch (AccountingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
