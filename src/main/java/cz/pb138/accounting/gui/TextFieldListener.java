package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.ContactType;
import cz.pb138.accounting.fn.InputType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldListener implements DocumentListener {

    private InputType input = null;
    private ContactType contact = null;
    private JTextField field;

    public TextFieldListener(JTextField field, InputType input) {
        this.input = input;
        initConstruct(field);
    }

    public TextFieldListener(JTextField field, ContactType contact) {
        this.contact = contact;
        initConstruct(field);
    }

    private void initConstruct(JTextField field) {
        this.field = field;
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

    }
}
