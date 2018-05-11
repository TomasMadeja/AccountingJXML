package cz.pb138.accounting.fn;

import cz.pb138.accounting.db.ADBErrorCodes;
import cz.pb138.accounting.db.AccountingDatabase;
import cz.pb138.accounting.db.AccountingException;

import java.util.Map;
import java.util.regex.Pattern;

public class AccountingFnImpl {

    private AccountingDatabase db;

    Map<Integer, Pattern> regexes;

    public AccountingFnImpl(AccountingDatabase db) {
        // Set server
        this.db = db;

        regexes.put()
    }

//    private void duck() throws AccountingException {
//        if (!quack()) {
//            throw new AccountingException(ADBErrorCodes.WRONG_INPUT, "");
//        }
//    }
//
//    private boolean quack() {
//        switch (1) {
//            case 1:
//                return true;
//                default:
//                    return false;
//
//        }
//    }

    public boolean matchInputs(String arg, InputType type) {
        switch (type) {
            case NAME:
                return arg.matches("^[A-Za-z ]*$");
            case ADDRESS:
                return arg.matches("^[A-Za-z ,0-9]*$");
            case ICO:
                return arg.matches("^[0-9]*$");
            case DIC:
                return arg.matches("^[A-Z]{2}[0-9]+$");
            case BANK:
                return arg.matches("^[0-9]{0,6}[-]*[0-9]{1,10}/[0-9]{4}$");
            default:
                return false;
        }
    }

    public boolean matchInputs(String arg, ContactType type) {
        switch (type) {
            case EMAIL:
                return arg.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
            case TELEPHONE:
                return arg.matches("^\\+[0-9]{12}$");
            default:
                return false;
        }
    }

    public void setName(String name) throws AccountingException {
        if (!matchInputs(name, InputType.NAME)) {
            throw new AccountingException(ADBErrorCodes.WRONG_INPUT_NAME, "Name contains only spaces and alphabet.");
        }
        db.getOwner().changeValue("name", name);
    }

    public void setAddress(String address) throws AccountingException {
        if (!matchInputs(address, InputType.ADDRESS)) {
            throw new AccountingException(ADBErrorCodes.WRONG_INPUT_ADDRESS, "Address contains \"A-Z,a-z\".");
        }
        db.getOwner().changeValue("address", address);
    }

    public void setICO(String ico) throws AccountingException {
        if (!matchInputs(ico, InputType.ICO)) {
            throw new AccountingException(ADBErrorCodes.WRONG_INPUT_ICO, "ICO contains numbers only.");
        }
        db.getOwner().changeValue("ico", ico);
    }

    public void setDIC(String dic) throws AccountingException {
        if (!matchInputs(dic, InputType.DIC)) {
            throw new AccountingException(ADBErrorCodes.WRONG_INPUT_DIC, "DIC example is CZ0001111. You have wrong format.");
        }
        db.getOwner().changeValue("dic", dic);
    }

    public void setBank(String bank) throws AccountingException {
        if (!matchInputs(bank, InputType.BANK)) {
            throw new AccountingException(ADBErrorCodes.WRONG_INPUT_BANK, "Bank information example is 000022-11231313/1132. Prefixed number is optional. Bank code is always 4 numbers.");
        }
        db.getOwner().changeValue("bank-information", bank);
    }

    public void setNote(String note) {
        db.getOwner().changeValue("note", note);
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
