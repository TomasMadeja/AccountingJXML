package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;

public class AccountingTFListen implements ChangeListener<String> {

    private InputType input;
    private ContactType contact;
    private ItemsType items;
    private DateType date;

    private AccountingFnImpl fn;
    private TextField warn;

    private String outMatch;

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       InputType input) {
        initVars(fn, warn, null, input, null, null);
    }

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       ContactType contact) {
        initVars(fn, warn, contact, null, null, null);
    }

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       ItemsType items) {
        initVars(fn, warn, null, null, items, null);
    }

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       DateType date) {
        initVars(fn, warn, null, null, null, date);
    }

    private void initVars(AccountingFnImpl fn,
                          TextField warn,
                          ContactType contact,
                          InputType input,
                          ItemsType items,
                          DateType date) {
        this.fn = fn;
        this.warn = warn;
        this.contact = contact;
        this.input = input;
        this.items = items;
        this.date = date;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue.length() > 0) {
            if (input != null) {
                outMatch = fn.matchPoint(newValue, input);
            } else if (contact != null) {
                outMatch = fn.matchPoint(newValue, contact);
            } else if (items != null) {
                outMatch = fn.matchPoint(newValue, items);
            } else {
                outMatch = fn.matchPoint(newValue, date);
            }
        }

        if (newValue.length() > 0 && outMatch.trim().length() > 0) {
            warn.setText(outMatch);
            warn.setDisable(false);
            warn.setVisible(true);
        } else {
            warn.setText("");
            warn.setDisable(true);
            warn.setVisible(false);
        }
    }
}
