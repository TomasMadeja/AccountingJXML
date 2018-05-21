package cz.pb138.accounting.fn;

import cz.pb138.accounting.db.ADBErrorCodes;
import cz.pb138.accounting.db.AccountingDatabase;
import cz.pb138.accounting.db.AccountingException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AccountingFnImpl {

    private AccountingDatabase db;

    private Map<Integer, Pattern> regexes = new HashMap<>();

    public AccountingFnImpl() {
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

    public String matchPoint(String arg, InputType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        String prefix = "Wrong input [" + type.toString().toLowerCase() + "]: ";

        switch (type) {
            case NAME:
                return prefix + "A-Z, a-z and spaces";
            case ADDRESS:
                return prefix + "A-Z, a-z, 0-9, spaces and comma";
            case ICO:
                return prefix + "Numbers";
            case DIC:
                return prefix + "Example CZ00001111";
            case BANK:
                return prefix + "Example 00000-1111111/3333";
            default:
                return "";
        }
    }

    public String matchPoint(String arg, ContactType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        String prefix = "Wrong input [" + type.toString().toLowerCase() + "]: ";

        switch (type) {
            case TELEPHONE:
                return prefix + "Example +420722633544";
            case EMAIL:
                return prefix + "Wrong format";
            default:
                return "";
        }
    }

    public String matchPoint(String arg, ItemsType type) {
        if (matchInputs(arg, type)) {
            return "";
        }

        String prefix = "Wrong input [" + type.toString().toLowerCase() + "]: ";

        switch (type) {
            case NAME:
                return prefix + "A-Z, a-z and spaces";
            case UNIT:
                return prefix + "Only a-z";
            case PRICE:
                return prefix + "Only numbers";
            case QUANTITY:
                return prefix + "Only numbers";
            default:
                return "";
        }
    }

    public boolean matchInputs(String arg, InputType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    public boolean matchInputs(String arg, ContactType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    public boolean matchInputs(String arg, ItemsType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }
}
