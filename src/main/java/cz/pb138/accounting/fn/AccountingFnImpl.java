package cz.pb138.accounting.fn;

import cz.pb138.accounting.db.AccountingDatabase;
import cz.pb138.accounting.db.Record;
import cz.pb138.accounting.db.impl.AccountingDatabaseImpl;
import cz.pb138.accounting.db.impl.AccountingException;
import cz.pb138.accounting.gui.ContactTable;
import cz.pb138.accounting.gui.ItemTable;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Functionality class.
 */
public class AccountingFnImpl implements AccountingFn {

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
                Pattern.compile("^.*[a-zA-Z].*$"));
        regexes.put(getIntType(InputType.ADDRESS),
                Pattern.compile("^[A-Za-z ,0-9.\\-]+$"));
        regexes.put(getIntType(InputType.ICO),
                Pattern.compile("^\\d{8}$"));
        regexes.put(getIntType(InputType.DIC),
                Pattern.compile("^CZ\\d{8,10}|SK\\d{10}$"));
        regexes.put(getIntType(InputType.BANK),
                Pattern.compile("^\\d{4}-\\d{10}\\/\\d{4}|" +
                        "[A-Z]{2}\\d{2}[0-9A-Z]{11,30}$"));
        regexes.put(getIntType(ContactType.EMAIL),
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+" +
                        "\\.[A-Za-z]{2,6}$"));
        regexes.put(getIntType(ContactType.TELEPHONE),
                Pattern.compile("^\\+[0-9]{12}$"));

        regexes.put(getIntType(ItemsType.NAME),
                Pattern.compile("^.*[a-zA-Z].*$"));
        regexes.put(getIntType(ItemsType.UNIT),
                Pattern.compile("^[a-z]+$"));
        regexes.put(getIntType(ItemsType.PRICE),
                Pattern.compile("^[0-9]+|[0-9]+.[0-9]+$"));
        regexes.put(getIntType(ItemsType.QUANTITY),
                Pattern.compile("^[0-9]+|[0-9]+.[0-9]+$"));

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

    public String matchPoint(String arg, InputType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        switch (type) {
            case NAME:
                return "Name must contain at least one letter.";
            case ADDRESS:
                return "Letters and numbers.";
            case ICO:
                return "8 numbers.";
            case DIC:
                return "Example CZ00001111.";
            case BANK:
                return "Bank number or IBAN.";
            default:
                return "";
        }
    }

    public String matchPoint(String arg, ContactType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        switch (type) {
            case TELEPHONE:
                return "Example +420722633544.";
            case EMAIL:
                return "Wrong email format.";
            default:
                return "";
        }
    }

    public String matchPoint(String arg, ItemsType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        switch (type) {
            case NAME:
                return "Name must contain at least one letter.";
            case UNIT:
                return "Only low letters.";
            case PRICE:
                return "Only numbers.";
            case QUANTITY:
                return "Only numbers.";
            default:
                return "";
        }
    }

    public String matchPoint(String arg, DateType date) {
        if (matchInputs(arg, date)) {
            return "";
        }

        switch (date) {
            case DATE:
                return "Example 12/24/1989.";
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
                return commitMe();
            } catch (AccountingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateName(String arg) {
        return updater(arg, "name", InputType.NAME);
    }

    public boolean updateAddress(String arg) {
        return updater(arg, "address", InputType.ADDRESS);
    }

    public boolean updateICO(String arg) {
        return updater(arg, "ico", InputType.ICO);
    }

    public boolean updateDIC(String arg) {
        return updater(arg, "dic", InputType.DIC);
    }

    public boolean updateBank(String arg) {
        return updater(arg, "bank-information", InputType.BANK);
    }

    public boolean updateNote(String arg) {
        try {
            db.getOwner().changeValue("note", arg);
        } catch (AccountingException e) {
            e.printStackTrace();
        }
        return commitMe();
    }

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
                matchInputs(billingDate, DateType.DATE) &&
                contacts.size() > 0 &&
                items.size() > 0
                ) {
            Record record;
            if (isPayer) {
                record = db.addExpenditure();
            } else {
                record = db.addRevenue();
            }

            for (ItemTable item : items) {
                if (matchInputs(item.getNameVal(), ItemsType.NAME) &&
                        matchInputs(item.getUnit(), ItemsType.UNIT) &&
                        matchInputs(item.getQuantity(), ItemsType.QUANTITY) &&
                        matchInputs(item.getPrice(), ItemsType.PRICE)) {
                    record.addItem(
                            item.getNameVal(),
                            item.getDesc(),
                            item.getQuantity(),
                            item.getUnit(),
                            item.getPrice());
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

                record.changeValue("issuing-date", getDate(issuingDate));
                record.changeValue("billing-date", getDate(billingDate));

                return commitMe();
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

    public boolean getPDF(String out) {

        try {
            if (db.getRecordsBetweenBilling(
                    "0000-01-01",
                    "3000-01-01").size() == 0) {
                return false;
            }
        } catch (AccountingException e) {
            e.printStackTrace();
        }

        File file = new File(out);

        if (!file.isDirectory()) {
            out = "";
        } else {
            out += "/";
        }

        try {
            String data = AccountingXSLT.getHTML(db.dbAsString());
            String[] htmls;
            if (data != null) {
                htmls = data.split(
                    "<!-- Nobody expects the spanish inquisition! -->");
            } else {
                return false;
            }

            for (int i = 0; i < (htmls.length - 1); i++) {
                AccountingPDF.savePDF(htmls[i], out +
                        "invoice_" + (i + 1) + ".pdf");
            }
            return true;
        } catch (AccountingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String summarizeMoney(String after, String before) {
        if (after.trim().length() == 0 ||
                before.trim().length() == 0) {
            return null;
        }

        if (!matchInputs(after, DateType.DATE) ||
                !matchInputs(before, DateType.DATE)) {
            return null;
        }

        try {
            return Double.toString(
                    (db.getEarningsByIssuingDate(
                            getDate(after),
                            getDate(before)) -
                    db.getLossesByIssuingDate(
                            getDate(after),
                            getDate(before))
                    ));
        } catch (AccountingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDate(String date) {
        String[] dateA = date.split("/");

        return dateA[2] + "-" +
                expandDate(dateA[0]) + "-" + expandDate(dateA[1]);
    }
}
