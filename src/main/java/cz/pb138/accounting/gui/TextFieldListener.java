package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.AccountingFnImpl;
import cz.pb138.accounting.fn.ContactType;
import cz.pb138.accounting.fn.InputType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldListener implements DocumentListener {

    private InputType input = null;
    private ContactType contact = null;
    private JLabel field;
    private AccountingFnImpl fn;
    private JTextField text;

    public TextFieldListener(AccountingFnImpl fn, JTextField text, JLabel field, InputType input) {
        this.input = input;
        initConstruct(fn, text, field);
    }

    public TextFieldListener(AccountingFnImpl fn, JTextField text, JLabel field, ContactType contact) {
        this.contact = contact;
        initConstruct(fn, text, field);
    }

    private void initConstruct(AccountingFnImpl fn, JTextField text, JLabel field) {
        this.field = field;
        this.fn = fn;
        this.text = text;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        actionTyping(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        actionTyping(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        actionTyping(e);
    }

    private void actionTyping(DocumentEvent e) {
        if (input != null) {
            field.setText(fn.matchPoint(text.getText(), input));
        } else {
            field.setText(fn.matchPoint(text.getText(), contact));
        }
    }
}
