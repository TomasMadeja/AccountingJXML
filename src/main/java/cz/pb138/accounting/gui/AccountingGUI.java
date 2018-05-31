package cz.pb138.accounting.gui;

import cz.pb138.accounting.db.AccountingDatabase;
import cz.pb138.accounting.db.AccountingException;
import cz.pb138.accounting.fn.*;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

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
    @FXML private Button btOwnerSave;
    @FXML private Button btOwnerReset;

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

    // Contacts - tab 2
    @FXML private Pane pnRecordContacts;

    @FXML private TableView tvRecordContacts;
    @FXML private TextField tfRecordAddContactEmail;
    @FXML private Button btRecordAddContactEmail;
    @FXML private TextField tfRecordWarnContactEmail;
    @FXML private TextField tfRecordAddContactTelephone;
    @FXML private Button btRecordAddContactTelephone;
    @FXML private TextField tfRecordWarnContactTelephone;

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
    @FXML private Button btRecordCreateInvoice;

    // Help variables
    private Map<Pane, Integer> menuPanes = new HashMap<>();

    @FXML
    protected void initialize() throws AccountingException {
        fn = new AccountingFnImpl();

        initPanes();
        initMenu();

        initListeners();

        initOwner();
    }

    public void killDatabase() {
        fn.killDatabase();
    }

    @FXML
    private void initOwner() {
        tfOwnerName.setText(fn.getOwner("name"));
        tfOwnerAddress.setText(fn.getOwner("address"));
        tfOwnerICO.setText(fn.getOwner("ico"));
        tfOwnerDIC.setText(fn.getOwner("dic"));
        tfOwnerBank.setText(fn.getOwner("bank-information"));
        tfOwnerNote.setText(fn.getOwner("note"));

        ObservableList<ContactTable> ownerContacts = FXCollections.observableArrayList();
        String[] contacts = fn.getOwnerContact("email");
        if (contacts == null) {
            return;
        }
        for (String arg : contacts) {
            ownerContacts.add(new ContactTable("email", arg));
        }

        tcOwnerType.setCellValueFactory(new PropertyValueFactory<ContactTable, String>("type"));
        tcOwnerVal.setCellValueFactory(new PropertyValueFactory<ContactTable, String>("value"));

        tcOwnerDelete.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        tcOwnerDelete.setCellFactory(param -> new TableCell<ContactTable, ContactTable>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(ContactTable row, boolean empty) {
                super.updateItem(row, empty);

                if (row == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> getTableView().getItems().remove(row));
            }
        });

        tvOwnerContacts.setItems(ownerContacts);
    }

    private void initPanes() {
        // Put all panes to map
        menuPanes.put(pnOwnerPersonal, 0);
        menuPanes.put(pnOwnerContacts, 0);
        menuPanes.put(pnRecordTrader, 1);
        menuPanes.put(pnRecordContacts, 1);
        menuPanes.put(pnRecordItems, 1);
        menuPanes.put(pnRecordInvoiceType, 1);
    }

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

    private void initListeners() {
        tfListen(tfOwnerName, tfOwnerWarnName, InputType.NAME);
        tfListen(tfOwnerAddress, tfOwnerWarnAddress, InputType.ADDRESS);
        tfListen(tfOwnerICO, tfOwnerWarnICO, InputType.ICO);
        tfListen(tfOwnerDIC, tfOwnerWarnDIC, InputType.DIC);
        tfListen(tfOwnerBank, tfOwnerWarnBank, InputType.BANK);

        tfListen(tfOwnerAddContactEmail, tfOwnerWarnContactEmail, ContactType.EMAIL);
        tfListen(tfOwnerAddContactTelephone, tfOwnerWarnContactTelephone, ContactType.TELEPHONE);

        // Tab 2
        tfListen(tfRecordName, tfRecordWarnName, InputType.NAME);
        tfListen(tfRecordAddress, tfRecordWarnAddress, InputType.ADDRESS);
        tfListen(tfRecordICO, tfRecordWarnICO, InputType.ICO);
        tfListen(tfRecordDIC, tfRecordWarnDIC, InputType.DIC);
        tfListen(tfRecordBank, tfRecordWarnBank, InputType.BANK);

        tfListen(tfRecordAddContactEmail, tfRecordWarnContactEmail, ContactType.EMAIL);
        tfListen(tfRecordAddContactTelephone, tfRecordWarnContactTelephone, ContactType.TELEPHONE);

        tfListen(tfRecordItemName, tfRecordWarnItemName, ItemsType.NAME);
        tfListen(tfRecordItemUnit, tfRecordWarnItemUnit, ItemsType.UNIT);
        tfListen(tfRecordItemPrice, tfRecordWarnItemPrice, ItemsType.PRICE);
        tfListen(tfRecordItemQuantity, tfRecordWarnItemQuantity, ItemsType.QUANTITY);

        tfListen(dpRecordBillingDate, tfRecordWarnBillingDate, DateType.DATE);
        tfListen(dpRecordIssuingDate, tfRecordWarnIssuingDate, DateType.DATE);

        tfListen(tfRecordRecAdd, tfRecordWarnRecAdd, InputType.ADDRESS);
    }

    private void tfListen(TextField theField, TextField theWarn, InputType input) {
        theField.textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                input));
    }

    private void tfListen(TextField theField, TextField theWarn, ContactType contact) {
        theField.textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                contact));
    }

    private void tfListen(TextField theField, TextField theWarn, ItemsType items) {
        theField.textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                items));
    }

    private void tfListen(DatePicker theField, TextField theWarn, DateType date) {
        theField.getEditor().textProperty().addListener(new AccountingTFListen(
                fn,
                theWarn,
                date));
    }
}
