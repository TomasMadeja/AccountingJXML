package cz.pb138.accounting.fn;

import cz.pb138.accounting.db.ADBErrorCodes;
import cz.pb138.accounting.db.AccountingDatabase;
import cz.pb138.accounting.db.AccountingDatabaseImpl;
import cz.pb138.accounting.db.AccountingException;
import cz.pb138.accounting.gui.ContactTable;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AccountingFnImpl {

    private AccountingDatabase db;

    private Map<Integer, Pattern> regexes = new HashMap<>();

    // Set server
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";

    public AccountingFnImpl() throws AccountingException {
        // Start server embedded mode
        db = new AccountingDatabaseImpl(USERNAME, PASSWORD);

        // Check existence of owner
        if (!db.isOwnerSet()) {
            db.createOwner();
            db.commitChanges();
        }

        initRegexes();
    }

    public void killDatabase() {
        try {
            db.killDatabase();
        } catch (AccountingException e) {
            e.printStackTrace();
        }
    }

    private void initRegexes() {
        regexes.put(getIntType(InputType.NAME), Pattern.compile("^[A-Za-z ]+$"));
        regexes.put(getIntType(InputType.ADDRESS), Pattern.compile("^[A-Za-z ,0-9]+$"));
        regexes.put(getIntType(InputType.ICO), Pattern.compile("^[0-9]+$"));
        regexes.put(getIntType(InputType.DIC), Pattern.compile("^[A-Z]{2}[0-9]+$"));
        regexes.put(getIntType(InputType.BANK), Pattern.compile("^[0-9]{0,6}[-]*[0-9]{1,10}/[0-9]{4}$"));
        regexes.put(getIntType(ContactType.EMAIL), Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"));
        regexes.put(getIntType(ContactType.TELEPHONE), Pattern.compile("^\\+[0-9]{12}$"));

        regexes.put(getIntType(ItemsType.NAME), Pattern.compile("^[A-Za-z ]+$"));
        regexes.put(getIntType(ItemsType.UNIT), Pattern.compile("^[a-z]+$"));
        regexes.put(getIntType(ItemsType.PRICE), Pattern.compile("^[0-9]+$"));
        regexes.put(getIntType(ItemsType.QUANTITY), Pattern.compile("^[0-9]+$"));

        regexes.put(getIntType(DateType.DATE), Pattern.compile("^([1-9]|1[0-2])/([1-9]|[12][0-9]|3[01])/\\d{4}$"));
    }

    public void setDB(AccountingDatabase db) {
        this.db = db;
    }

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

    private boolean updater(String arg, String select, InputType input) {
        if (matchInputs(arg, input)) {
            db.getOwner().changeValue(select, arg);

            return commitMe();
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
        db.getOwner().changeValue("note", arg);

        return commitMe();
    }

    public boolean updateContacts(ObservableList<ContactTable> list, ObservableList<ContactTable> delList) {
        if (delList.size() > 0) {
            for (ContactTable ele : delList) {
                db.getOwner().removeContact(ele.getType(), ele.getValue());
            }
        }

        if (list.size() > 0) {
            for (ContactTable elem : list) {
                if (!elem.getInDatabase()) {
                    if ((matchInputs(elem.getValue(), ContactType.EMAIL) && elem.getType().equals("email")) ||
                            (matchInputs(elem.getValue(), ContactType.TELEPHONE) && elem.getType().equals("telephone"))) {
                        db.getOwner().addContact(elem.getType(), elem.getValue());
                    }
                }
            }
        }

        return commitMe();
    }

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
