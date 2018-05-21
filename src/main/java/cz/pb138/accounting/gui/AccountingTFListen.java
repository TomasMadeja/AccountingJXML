package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.AccountingFnImpl;
import cz.pb138.accounting.fn.ContactType;
import cz.pb138.accounting.fn.InputType;
import cz.pb138.accounting.fn.ItemsType;
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

    private AccountingFnImpl fn;
    private TextField warn;

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       InputType input) {
        initVars(fn, warn);
        this.input = input;
        this.contact = null;
        this.items = null;
    }

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       ContactType contact) {
        initVars(fn, warn);
        this.contact = contact;
        this.input = null;
        this.items = null;
    }

    AccountingTFListen(AccountingFnImpl fn,
                       TextField warn,
                       ItemsType items) {
        initVars(fn, warn);
        this.contact = null;
        this.input = null;
        this.items = items;
    }

    private void initVars(AccountingFnImpl fn,
                          TextField warn) {
        this.fn = fn;
        this.warn = warn;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        String outMatch;
        if (input != null) {
            outMatch = fn.matchPoint(newValue, input);
        } else if(contact != null) {
            outMatch = fn.matchPoint(newValue, contact);
        } else {
            outMatch = fn.matchPoint(newValue, items);
        }

        if (outMatch.trim().length() > 0) {
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
