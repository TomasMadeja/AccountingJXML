package cz.pb138.accounting.gui;

import cz.pb138.accounting.db.AccountingException;
import cz.pb138.accounting.fn.*;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for FXML.
 */
public class AccountingGUI {

    // Functionality
    private AccountingFnImpl fn;

    // Personal data - tab 1
    @FXML private Pane pnOwnerPersonal;

    @FXML private TextField tfOwnerName;
    @FXML private TextField tfOwnerAddress;
    @FXML private TextField tfOwnerICO;
    @FXML private TextField tfOwnerDIC;
    @FXML private TextField tfOwnerBank;
    @FXML private TextArea tfOwnerNote;

    @FXML private TextField tfOwnerWarnName;
    @FXML private TextField tfOwnerWarnAddress;
    @FXML private TextField tfOwnerWarnICO;
    @FXML private TextField tfOwnerWarnDIC;
    @FXML private TextField tfOwnerWarnBank;

    // Contacts - tab 1
    @FXML private Pane pnOwnerContacts;

    @FXML private TableView<ContactTable> tvOwnerContacts;
    @FXML private TableColumn<ContactTable, String> tcOwnerType;
    @FXML private TableColumn<ContactTable, String> tcOwnerVal;
    @FXML private TableColumn<ContactTable, ContactTable> tcOwnerDelete;

    @FXML private TextField tfOwnerAddContactEmail;
    @FXML private Button btOwnerAddContactEmail;
    @FXML private TextField tfOwnerWarnContactEmail;
    @FXML private TextField tfOwnerAddContactTelephone;
    @FXML private Button btOwnerAddContactTelephone;
    @FXML private TextField tfOwnerWarnContactTelephone;

    // Menu - tab 1
    @FXML private Button btOwnerPersonal;
    @FXML private Button btOwnerContacts;

    // Tabs
    @FXML private Tab tbOwner;
    @FXML private Tab tbInvoice;

    // Trader - tab 2
    @FXML private Pane pnRecordTrader;

    @FXML private TextField tfRecordName;
    @FXML private TextField tfRecordAddress;
    @FXML private TextField tfRecordICO;
    @FXML private TextField tfRecordDIC;
    @FXML private TextField tfRecordBank;
    @FXML private TextArea tfRecordNote;

    @FXML private TextField tfRecordWarnName;
    @FXML private TextField tfRecordWarnAddress;
    @FXML private TextField tfRecordWarnICO;
    @FXML private TextField tfRecordWarnDIC;
    @FXML private TextField tfRecordWarnBank;

    @FXML private RadioButton rbRecordSeller;
    @FXML private RadioButton rbRecordPayer;

    // Contacts - tab 2
    @FXML private Pane pnRecordContacts;

    @FXML private TableView tvRecordContacts;
    @FXML private TextField tfRecordAddContactEmail;
    @FXML private Button btRecordAddContactEmail;
    @FXML private TextField tfRecordWarnContactEmail;
    @FXML private TextField tfRecordAddContactTelephone;
    @FXML private Button btRecordAddContactTelephone;
    @FXML private TextField tfRecordWarnContactTelephone;

    @FXML private TableColumn<ContactTable, String> tcRecordConType;
    @FXML private TableColumn<ContactTable, String> tcRecordConVal;
    @FXML private TableColumn<ContactTable, ContactTable> tcRecordConDelete;

    // Items - tab 2
    @FXML private Pane pnRecordItems;

    @FXML private TableView tvRecordItems;
    @FXML private TextField tfRecordItemName;
    @FXML private TextField tfRecordItemUnit;
    @FXML private TextField tfRecordItemPrice;
    @FXML private TextField tfRecordItemQuantity;
    @FXML private TextField tfRecordItemDesc;
    @FXML private TextField tfRecordWarnItemName;
    @FXML private TextField tfRecordWarnItemUnit;
    @FXML private TextField tfRecordWarnItemPrice;
    @FXML private TextField tfRecordWarnItemQuantity;

    @FXML private TableColumn<ItemTable, String> tcRecordItemName;
    @FXML private TableColumn<ItemTable, String> tcRecordItemQuant;
    @FXML private TableColumn<ItemTable, String> tcRecordItemUnit;
    @FXML private TableColumn<ItemTable, String> tcRecordItemPrice;
    @FXML private TableColumn<ItemTable, ItemTable> tcRecordItemDelete;

    // Invoice type
    @FXML private Pane pnRecordInvoiceType;
    @FXML private DatePicker dpRecordBillingDate;
    @FXML private DatePicker dpRecordIssuingDate;
    @FXML private TextField tfRecordRecAdd;
    @FXML private TextField tfRecordWarnBillingDate;
    @FXML private TextField tfRecordWarnIssuingDate;
    @FXML private TextField tfRecordWarnRecAdd;

    // Menu - tab 2
    @FXML private Button btRecordTrader;
    @FXML private Button btRecordContacts;
    @FXML private Button btRecordItems;
    @FXML private Button btRecordInvoiceType;

    // Help variables
    private Map<Pane, Integer> menuPanes = new HashMap<>();

    // Owner contact table
    private ObservableList<ContactTable> ownerContacts =
            FXCollections.observableArrayList();
    private ObservableList<ContactTable> ownerDeletedContacts =
            FXCollections.observableArrayList();

    // Record tables
    private ObservableList<ContactTable> recordContacts =
            FXCollections.observableArrayList();
    private ObservableList<ItemTable> recordItems =
            FXCollections.observableArrayList();

    /**
     * Constructor.
     * @throws AccountingException error
     */
    @FXML
    protected void initialize() throws AccountingException {
        fn = new AccountingFnImpl();

        initPanes();
        initMenu();

        initListeners();

        initOwner();
    }

    /**
     * Kill database.
     */
    public void killDatabase() {
        fn.killDatabase();
    }

    /**
     * Fill fields from database.
     */
    @FXML
    private void initOwner() {
        tfOwnerName.setText(fn.getOwner("name"));
        tfOwnerAddress.setText(fn.getOwner("address"));
        tfOwnerICO.setText(fn.getOwner("ico"));
        tfOwnerDIC.setText(fn.getOwner("dic"));
        tfOwnerBank.setText(fn.getOwner("bank-information"));
        tfOwnerNote.setText(fn.getOwner("note"));

        ownerContacts.clear();
        ownerDeletedContacts.clear();

        getContacts("email", ownerContacts);
        getContacts("telephone", ownerContacts);

        setContactTable(tcOwnerType, tcOwnerVal, tcOwnerDelete);
    }

    /**
     * Click on Save changes.
     */
    @FXML
    private void ownerSaveChanges() {
        if (fn.updateName(tfOwnerName.getText())) {
            // TODO
        }
        if (fn.updateAddress(tfOwnerAddress.getText())) {
            // TODO
        }
        if (fn.updateICO(tfOwnerICO.getText())) {
            // TODO
        }
        if (fn.updateDIC(tfOwnerDIC.getText())) {
            // TODO
        }
        if (fn.updateBank(tfOwnerBank.getText())) {
            // TODO
        }
        if (fn.updateNote(tfOwnerNote.getText())) {
            // TODO
        }

        if (fn.updateContacts(ownerContacts, ownerDeletedContacts)) {
            // TODO
        }
    }

    /**
     * Click on Create invoice.
     */
    @FXML
    private void recordCreateInvoice() {
        clearInvoice();
    }

    /**
     * Click on Add email.
     */
    @FXML
    private void ownerAddEmail() {
        ownerAddContact(
                tfOwnerAddContactEmail,
                ContactType.EMAIL,
                "email"
        );
    }

    /**
     * Click on Add telephone.
     */
    @FXML
    private void ownerAddTelephone() {
        ownerAddContact(
                tfOwnerAddContactTelephone,
                ContactType.TELEPHONE,
                "telephone"
        );
    }

    /**
     * Add contact to table.
     * @param tf field
     * @param contact type
     * @param type type string
     */
    private void ownerAddContact(TextField tf,
                                 ContactType contact,
                                 String type) {
        String arg = tf.getText();
        if (fn.matchPoint(arg, contact).equals("") &&
                !contactExist(arg, type, ownerContacts)) {
            ownerContacts.add(new ContactTable(type, arg, false));
            tf.setText("");
        }
    }

    /**
     * Listener for deletes.
     * @param del column
     * @param isOwner bool
     * @param <T> type of column
     */
    private <T> void setTableListener(TableColumn<T, T> del, Boolean isOwner) {
        del.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        del.setCellFactory(param -> new TableCell<T, T>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(T row, boolean empty) {
                super.updateItem(row, empty);

                if (row == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> {
                    getTableView().getItems().remove(row);
                    if (isOwner && row instanceof ContactTable &&
                            ((ContactTable) row).getInDatabase()) {
                        ownerDeletedContacts.add((ContactTable) row);
                    }
                });
            }
        });
    }

    /**
     * Fill contact tables.
     * @param type
     * @param val
     * @param del
     */
    private void setContactTable(TableColumn<ContactTable, String> type,
                                 TableColumn<ContactTable, String> val,
                                 TableColumn<ContactTable, ContactTable> del) {
        type.setCellValueFactory(new PropertyValueFactory<
                ContactTable,
                String
                >("type"));
        val.setCellValueFactory(new PropertyValueFactory<
                ContactTable,
                String
                >("value"));

        // TODO

        setTableListener(tcOwnerDelete, true);

        tvOwnerContacts.setItems(ownerContacts);
    }

    /**
     * Get contacts from database.
     * @param arg string
     * @param list table
     */
    private void getContacts(String arg, ObservableList<ContactTable> list) {
        String[] contacts = fn.getOwnerContact(arg);
        if (contacts == null) {
            return;
        }

        for (String ele : contacts) {
            if (!contactExist(ele, arg, list)) {
                list.add(new ContactTable(arg, ele, true));
            }
        }
    }

    /**
     * Find a contact in ObservableList.
     * Cannot add duplicities to table.
     * @param val string
     * @param arg string
     * @param list table
     * @return bool
     */
    private boolean contactExist(String val,
                                 String arg,
                                 ObservableList<ContactTable> list) {
        for (ContactTable ele : list) {
            if (ele.getType().equals(arg) && ele.getValue().equals(val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pane groups.
     */
    private void initPanes() {
        // Put all panes to map
        menuPanes.put(pnOwnerPersonal, 0);
        menuPanes.put(pnOwnerContacts, 0);
        menuPanes.put(pnRecordTrader, 1);
        menuPanes.put(pnRecordContacts, 1);
        menuPanes.put(pnRecordItems, 1);
        menuPanes.put(pnRecordInvoiceType, 1);
    }

    /**
     * Menu initialization.
     */
    private void initMenu() {
        // Set listeners for menu
        // Tab 1
        menuSelect(btOwnerPersonal, pnOwnerPersonal);
        menuSelect(btOwnerContacts, pnOwnerContacts);

        // Tab 2
        menuSelect(btRecordTrader, pnRecordTrader);
        menuSelect(btRecordContacts, pnRecordContacts);
        menuSelect(btRecordItems, pnRecordItems);
        menuSelect(btRecordInvoiceType, pnRecordInvoiceType);
    }

    /**
     * Menu realization.
     * @param theButton button
     * @param thePane pane
     */
    private void menuSelect(Button theButton, Pane thePane) {
        theButton.setOnAction(event -> {
            Integer tab = menuPanes.get(thePane);
            for (Map.Entry<Pane, Integer> row : menuPanes.entrySet()) {
                if (row.getValue().equals(tab)) {
                    row.getKey().setVisible(false);
                    row.getKey().setDisable(true);
                }
            }
            thePane.setVisible(true);
            thePane.setDisable(false);
        });
    }

    /**
     * Initialization for warnings and fields.
     */
    private void initListeners() {
        tfListen(tfOwnerName, tfOwnerWarnName, InputType.NAME);
        tfListen(tfOwnerAddress, tfOwnerWarnAddress, InputType.ADDRESS);
        tfListen(tfOwnerICO, tfOwnerWarnICO, InputType.ICO);
        tfListen(tfOwnerDIC, tfOwnerWarnDIC, InputType.DIC);
        tfListen(tfOwnerBank, tfOwnerWarnBank, InputType.BANK);

        tfListen(
                tfOwnerAddContactEmail,
                tfOwnerWarnContactEmail,
                ContactType.EMAIL
        );
        tfListen(
                tfOwnerAddContactTelephone,
                tfOwnerWarnContactTelephone,
                ContactType.TELEPHONE
        );

        // Tab 2
        tfListen(tfRecordName, tfRecordWarnName, InputType.NAME);
        tfListen(tfRecordAddress, tfRecordWarnAddress, InputType.ADDRESS);
        tfListen(tfRecordICO, tfRecordWarnICO, InputType.ICO);
        tfListen(tfRecordDIC, tfRecordWarnDIC, InputType.DIC);
        tfListen(tfRecordBank, tfRecordWarnBank, InputType.BANK);

        tfListen(
                tfRecordAddContactEmail,
                tfRecordWarnContactEmail,
                ContactType.EMAIL
        );
        tfListen(
                tfRecordAddContactTelephone,
                tfRecordWarnContactTelephone,
                ContactType.TELEPHONE
        );

        tfListen(tfRecordItemName, tfRecordWarnItemName, ItemsType.NAME);
        tfListen(tfRecordItemUnit, tfRecordWarnItemUnit, ItemsType.UNIT);
        tfListen(tfRecordItemPrice, tfRecordWarnItemPrice, ItemsType.PRICE);
        tfListen(
                tfRecordItemQuantity,
                tfRecordWarnItemQuantity,
                ItemsType.QUANTITY
        );

        tfListen(dpRecordBillingDate, tfRecordWarnBillingDate, DateType.DATE);
        tfListen(dpRecordIssuingDate, tfRecordWarnIssuingDate, DateType.DATE);

        tfListen(tfRecordRecAdd, tfRecordWarnRecAdd, InputType.ADDRESS);
    }

    /**
     * Set listener for textField.
     * @param theField
     * @param theWarn
     * @param input
     */
    private void tfListen(TextField theField,
                          TextField theWarn,
                          InputType input) {
        theField.textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                input));
    }

    private void tfListen(TextField theField,
                          TextField theWarn,
                          ContactType contact) {
        theField.textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                contact));
    }

    private void tfListen(TextField theField,
                          TextField theWarn,
                          ItemsType items) {
        theField.textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                items));
    }

    private void tfListen(DatePicker theField,
                          TextField theWarn,
                          DateType date) {
        theField.getEditor().textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                date));
    }

    /**
     * Is called only if added invoice is correct.
     * Clear all fields in tab Invoices.
     */
    private void clearInvoice() {
        tfRecordName.setText("");
        tfRecordAddress.setText("");
        tfRecordICO.setText("");
        tfRecordDIC.setText("");
        tfRecordBank.setText("");
        tfRecordNote.setText("");

        rbRecordPayer.setSelected(false);
        rbRecordSeller.setSelected(false);

        recordContacts.clear();
        recordItems.clear();

        tfRecordAddContactEmail.setText("");
        tfRecordAddContactTelephone.setText("");

        tfRecordItemName.setText("");
        tfRecordItemUnit.setText("");
        tfRecordItemQuantity.setText("");
        tfRecordItemPrice.setText("");
        tfRecordItemDesc.setText("");

        tfRecordRecAdd.setText("");
        dpRecordBillingDate.getEditor().setText("");
        dpRecordIssuingDate.getEditor().setText("");
    }
}
