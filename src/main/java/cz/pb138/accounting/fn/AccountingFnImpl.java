package cz.pb138.accounting.fn;

import cz.pb138.accounting.db.AccountingDatabase;

public class AccountingFnImpl {

    private AccountingDatabase db;

    public AccountingFnImpl(AccountingDatabase db) {
        // Set server
        this.db = db;

    }

    public void setName(String name) throws Exception {
        if (!name.matches("[A-Za-z ]*")) {
            throw new Exception("Wrong name input.");
        }
        db.getOwner().changeValue("name", name);
    }

    public void setAddress(String address) throws Exception {
        if (!address.matches("A-Za-z ,0-9")) {
            throw new Exception("Wrong address input.");
        }
        db.getOwner().changeValue("address", address);
    }

    public void setICO(String ico) throws Exception {
        if (!ico.matches("0-9")) {
            throw new Exception("Wrong ICO input.");
        }
        db.getOwner().changeValue("ico", ico);
    }

    public void setDIC(String dic) throws Exception {

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
