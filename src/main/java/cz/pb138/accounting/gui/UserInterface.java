package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.AccountingFnImpl;
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
    private JTextField tfOwnerAddContact;
    private JComboBox cbOwnerContactTypes;
    private JButton btOwnerAddContact;
    private JButton btOwnerSave;

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
    private JTextField tfOwnerNote;
    private JButton btOwnerReset;
    private JRadioButton rbRecordPayer;
    private JRadioButton rbRecordSeller;
    private JTextField tfRecordBilling;
    private JTextField tfRecordIssuing;
    private JTextField tfRecordNameRec;
    private JButton btRecordSave;
    private JButton addItemButton;
    private JTextField textField16;
    private JTextField textField17;
    private JTextField textField18;
    private JTextField textField19;
    private JTextField tfRecordAddressRec;
    private JScrollPane spOwnerAddedContacts;
    private JScrollPane spRecordAddedContacts;
    private JLabel lbOwnerName;
    private JLabel lbOwnerAddress;
    private JLabel lbOwnerICO;
    private JLabel lbOwnerDIC;
    private JLabel lbOwnerBank;

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
        tfOwnerName.getDocument().addDocumentListener(new TextFieldListener(lbOwnerName, InputType.NAME));
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
