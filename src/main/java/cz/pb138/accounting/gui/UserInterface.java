package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.AccountingFnImpl;
import cz.pb138.accounting.fn.ContactType;
import cz.pb138.accounting.fn.InputType;

import javax.swing.*;
import java.awt.*;

public class UserInterface {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;

    // Tab 1
    private JTextField tfOwnerName;
    private JTextField tfOwnerAddress;
    private JTextField tfOwnerICO;
    private JTextField tfOwnerDIC;
    private JTextField tfOwnerBank;
    private JLabel lbOwnerName;
    private JLabel lbOwnerAddress;
    private JLabel lbOwnerICO;
    private JLabel lbOwnerDIC;
    private JLabel lbOwnerBank;
    private JTextField tfOwnerAddContact;
    private JComboBox cbOwnerContactTypes;
    private JButton btOwnerAddContact;
    private JButton btOwnerSave;
    private JTextField tfOwnerNote;
    private JButton btOwnerReset;
    private JScrollPane spOwnerAddedContacts;

    // Tab 2
    private JTextField tfRecordName;
    private JTextField tfRecordAddress;
    private JTextField tfRecordICO;
    private JTextField tfRecordDIC;
    private JTextField tfRecordBank;
    private JTextField tfRecordNote;
    private JTextField tfRecordAddContact;
    private JComboBox cbRecordContactTypes;
    private JButton btRecordAddContact;
    private JRadioButton rbRecordPayer;
    private JRadioButton rbRecordSeller;
    private JTextField tfRecordNameRec;
    private JButton btRecordSave;
    private JButton addItemButton;
    private JTextField tfRecordQuality;
    private JTextField tfRecordUnit;
    private JTextField tfRecordPrice;
    private JTextField tfRecordDesc;
    private JTextField tfRecordAddressRec;
    private JScrollPane spRecordAddedContacts;
    private JScrollPane spRecordItems;
    private JLabel lbRecordName;
    private JLabel lbRecordAddress;
    private JLabel lbRecordICO;
    private JLabel lbRecordDIC;
    private JLabel lbRecordBank;
    private JLabel lbRecordQuantity;
    private JLabel lbRecordUnit;
    private JLabel lbRecordPrice;

    // Functionality
    // Prepsat na Implementaci !!!!!
    private AccountingFnImpl fn;

    /**
     * Constructor
     * @param fn variable with AccountingDatabase
     */
    public UserInterface(AccountingFnImpl fn) {
        // Database functional
        this.fn = fn;

        // If information missing make tabs un-click-able
        if (!this.fn.checkOwnerIsGood()) enableTabs(false);

        // Listeners
        setLabels(tfOwnerName, lbOwnerName, InputType.NAME);
        setLabels(tfOwnerAddress, lbOwnerAddress, InputType.ADDRESS);
        setLabels(tfOwnerICO, lbOwnerICO, InputType.ICO);
        setLabels(tfOwnerDIC, lbOwnerDIC, InputType.DIC);
        setLabels(tfOwnerBank, lbOwnerBank, InputType.BANK);
    }

    private void setLabels(JTextField field, JLabel label, InputType input) {
        field.getDocument().addDocumentListener(new TextFieldListener(fn, field, label, input));
    }

    private void setLabels(JTextField field, JLabel label, ContactType contact) {
        field.getDocument().addDocumentListener(new TextFieldListener(fn, field, label, contact));
    }

    /**
     * Makes tabs click-able or not
     * @param a boolean
     */
    private void enableTabs(boolean a) {
        //tabbedPane1.setEnabledAt(1, a);
    }

    /**
     * Gets interface
     * @return JPanel number one
     */
    public JPanel getPanel1() {
        return panel1;
    }
}
