package cz.pb138.accounting.gui;

import cz.pb138.accounting.fn.AccountingFnImpl;

import javax.swing.*;

public class UserInterface {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JComboBox comboBox1;
    private JButton addContactButton;
    private JButton updateChangesButton;
    private JTable table1;

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

        //Listener

    }

    /**
     * Makes tabs click-able or not
     * @param a boolean
     */
    private void enableTabs(boolean a) {
        tabbedPane1.setEnabledAt(1, a);
        tabbedPane1.setEnabledAt(2, a);
    }

    /**
     * Gets interface
     * @return JPanel number one
     */
    public JPanel getPanel1() {
        return panel1;
    }
}
