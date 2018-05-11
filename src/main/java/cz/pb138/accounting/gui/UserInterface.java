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
    private JButton addButton1;
    private JButton updateChangesButton;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JTextField textField11;
    private JTextField textField12;
    private JTextField textField13;
    private JTextField textField14;
    private JComboBox comboBox2;
    private JButton saveChangesButton;
    private JButton addButton;
    private JTextField textField15;
    private JButton resetButton;
    private JRadioButton payerRadioButton;
    private JRadioButton sellerRadioButton;
    private JComboBox comboBox3;
    private JComboBox comboBox4;
    private JTextField textField20;
    private JTextField textField21;
    private JTextField textField22;
    private JButton addDocumentButton;
    private JButton addItemButton;
    private JTextField textField16;
    private JTextField textField17;
    private JTextField textField18;
    private JTextField textField19;
    private JTextField textField23;

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
