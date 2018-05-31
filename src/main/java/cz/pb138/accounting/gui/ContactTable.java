package cz.pb138.accounting.gui;

import javafx.beans.property.SimpleStringProperty;

public class ContactTable {
    private String type;
    private String value;

    public ContactTable(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
