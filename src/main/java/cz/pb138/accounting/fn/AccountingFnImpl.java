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

    public AccountingFnImpl(AccountingDatabase db) {
        // Set server
        this.db = db;

        regexes.put(getIntType(InputType.NAME), Pattern.compile("^[A-Za-z ]+$"));
        regexes.put(getIntType(InputType.ADDRESS), Pattern.compile("^[A-Za-z ,0-9]+$"));
        regexes.put(getIntType(InputType.ICO), Pattern.compile("^[0-9]+$"));
        regexes.put(getIntType(InputType.DIC), Pattern.compile("^[A-Z]{2}[0-9]+$"));
        regexes.put(getIntType(InputType.BANK), Pattern.compile("^[0-9]{0,6}[-]*[0-9]{1,10}/[0-9]{4}$"));
        regexes.put(getIntType(ContactType.EMAIL), Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"));
        regexes.put(getIntType(ContactType.TELEPHONE), Pattern.compile("^\\+[0-9]{12}$"));
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

    public String matchPoint(String arg, InputType type) {
        if (matchInputs(arg, type)) {
            return " ";
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
                return " ";
        }
    }

    public String matchPoint(String arg, ContactType type) {
        if (matchInputs(arg, type)) {
            return " ";
        }

        String prefix = "Wrong input [" + type.toString().toLowerCase() + "]: ";

        switch (type) {
            case TELEPHONE:
                return prefix + "Example +420722633544";
            case EMAIL:
                return prefix + "Wrong format";
            default:
                return " ";
        }
    }

    public boolean matchInputs(String arg, InputType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    public boolean matchInputs(String arg, ContactType type) {
        return regexes.get(type.getValue()).matcher(arg).matches();
    }

    private String getOwnerVal(String arg) {
        return db.getOwner().getValue(arg);
    }

    private String[] getOwnerCont(String arg) {
        return db.getOwner().getContact(arg);
    }

    public boolean checkOwnerIsGood() {
        return  !getOwnerVal("name").isEmpty() &&
                !getOwnerVal("address").isEmpty() &&
                !getOwnerVal("ico").isEmpty() &&
                !getOwnerVal("dic").isEmpty() &&
                !getOwnerVal("bank-information").isEmpty() &&
                getOwnerCont("email").length != 0 &&
                getOwnerCont("telephone").length != 0;
    }
}
