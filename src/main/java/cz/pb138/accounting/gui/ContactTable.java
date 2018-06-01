package cz.pb138.accounting.gui;

import javafx.beans.property.SimpleStringProperty;

public class ContactTable {
    private String type;
    private String value;
    private Boolean inDatabase;

    public ContactTable(String type, String value, Boolean inDatabase) {
        this.type = type;
        this.value = value;
        this.inDatabase = inDatabase;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Boolean getInDatabase() {
        return inDatabase;
    }
}
