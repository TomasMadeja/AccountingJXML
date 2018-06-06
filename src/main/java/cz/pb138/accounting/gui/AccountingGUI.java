package cz.pb138.accounting.gui;

import cz.pb138.accounting.db.impl.AccountingException;
import cz.pb138.accounting.fn.*;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for FXML.
 */
public class AccountingGUI {

    // Functionality
    private AccountingFn fn;

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
    @FXML private TextField tfOwnerWarnContactEmail;
    @FXML private TextField tfOwnerAddContactTelephone;
    @FXML private TextField tfOwnerWarnContactTelephone;

    // Menu - tab 1
    @FXML private Button btOwnerPersonal;
    @FXML private Button btOwnerContacts;

    @FXML private TextArea tfWarningSaveChanges;
    @FXML private TextArea tfWarningAddContact;

    // Tabs
//    @FXML private Tab tbOwner;
//    @FXML private Tab tbInvoice;

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

    @FXML private TableView<ContactTable> tvRecordContacts;
    @FXML private TextField tfRecordAddContactEmail;
    @FXML private TextField tfRecordWarnContactEmail;
    @FXML private TextField tfRecordAddContactTelephone;
    @FXML private TextField tfRecordWarnContactTelephone;

    @FXML private TableColumn<ContactTable, String> tcRecordConType;
    @FXML private TableColumn<ContactTable, String> tcRecordConVal;
    @FXML private TableColumn<ContactTable, ContactTable> tcRecordConDelete;

    // Items - tab 2
    @FXML private Pane pnRecordItems;

    @FXML private TableView<ItemTable> tvRecordItems;
    @FXML private TextField tfRecordItemName;
    @FXML private TextField tfRecordItemUnit;
    @FXML private TextField tfRecordItemPrice;
    @FXML private TextField tfRecordItemQuantity;
    @FXML private TextField tfRecordItemDesc;
    @FXML private TextField tfRecordWarnItemName;
    @FXML private TextField tfRecordWarnItemUnit;
    @FXML private TextField tfRecordWarnItemPrice;
    @FXML private TextField tfRecordWarnItemQuantity;

    @FXML private TextField tfWarningAddItem;

    @FXML private TableColumn<ItemTable, String> tcRecordItemName;
    @FXML private TableColumn<ItemTable, String> tcRecordItemQuant;
    @FXML private TableColumn<ItemTable, String> tcRecordItemUnit;
    @FXML private TableColumn<ItemTable, String> tcRecordItemPrice;
    @FXML private TableColumn<ItemTable, ItemTable> tcRecordItemDelete;
    @FXML private TableColumn<ItemTable, String> tcRecordItemDescription;

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

    @FXML private TextArea tfNotifyCreateInvoice;
    @FXML private TextArea tfWarningCreateInvoice;

    // Tools - tab 2
    @FXML private Label lbShowMoney;
    @FXML private DatePicker dpToolsFromDate;
    @FXML private DatePicker dpToolsToDate;
    @FXML private TextField tfFolderSave;
    @FXML private TextField tfToolsWarnToDate;
    @FXML private TextField tfToolsWarnFromDate;

    @FXML private TextField tfWarningExport;
    @FXML private TextField tfNotifyExport;
    @FXML private TextField tfWarningSummarize;

    // Help variables
    private Map<Pane, Integer> menuPanes = new HashMap<>();

    private Stage primaryStage;

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

    private FadeTransition hideNotice;

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
        initInvoiceTables();

        initTransition();

        formatDates(dpRecordBillingDate);
        formatDates(dpRecordIssuingDate);
        formatDates(dpToolsFromDate);
        formatDates(dpToolsToDate);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void formatDates(DatePicker picker) {
        picker.setConverter(new StringConverter<LocalDate>()
        {
            private DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern("M/d/yyyy");

            @Override
            public String toString(LocalDate localDate)
            {
                if(localDate==null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString)
            {
                if(dateString==null || dateString.trim().isEmpty())
                {
                    return null;
                }
                return LocalDate.parse(dateString,dateTimeFormatter);
            }
        });
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

        setContactTable(tcOwnerType, tcOwnerVal, tcOwnerDelete, true);
    }

    /**
     * Click on Save changes.
     */
    @FXML
    private void ownerSaveChanges() {
        Boolean quack = true;
        tfWarningSaveChanges.setText("");

        if (!fn.updateName(tfOwnerName.getText())) {
            tfWarningSaveChanges.appendText("Failed to add name ");
            quack = false;
        }
        if (!fn.updateAddress(tfOwnerAddress.getText())) {
            tfWarningSaveChanges.appendText("Failed to add address ");
            quack = false;
        }
        if (!fn.updateICO(tfOwnerICO.getText())) {
            tfWarningSaveChanges.appendText("Failed to add ICO ");
            quack = false;
        }
        if (!fn.updateDIC(tfOwnerDIC.getText())) {
            tfWarningSaveChanges.appendText("Failed to add DIC ");
            quack = false;
        }
        if (!fn.updateBank(tfOwnerBank.getText())) {
            tfWarningSaveChanges.appendText("Failed to add bank information ");
            quack = false;
        }
        if (!fn.updateNote(tfOwnerNote.getText())) {
            tfWarningSaveChanges.appendText("Failed to add note ");
            quack = false;
        }

        if (!fn.updateContacts(ownerContacts, ownerDeletedContacts)) {
            tfWarningSaveChanges.setText("Failed to add contact");
            quack = false;
        }

        if (!quack) {
            revealHideNotice(tfWarningSaveChanges);
        }

        // Lze vypisovat hlasky ze tu nebo onu polozku se nepovedlo pridat

    }

    /**
     * Prepare table for items and contacts.
     * Invoice tab.
     */
    private void initInvoiceTables() {
        tcRecordItemName.setCellValueFactory(
                new PropertyValueFactory<>("nameVal"));
        tcRecordItemDescription.setCellValueFactory(
                new PropertyValueFactory<>("desc"));
        tcRecordItemQuant.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));
        tcRecordItemUnit.setCellValueFactory(
                new PropertyValueFactory<>("unit"));
        tcRecordItemPrice.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        setTableListener(tcRecordItemDelete, false);

        tvRecordItems.setItems(recordItems);

        setContactTable(tcRecordConType,
                tcRecordConVal,
                tcRecordConDelete,
                false);
    }

    /**
     * Click on Create invoice.
     */
    @FXML
    private void recordCreateInvoice() {
        if (!rbRecordPayer.isSelected() && !rbRecordSeller.isSelected()){
            tfWarningCreateInvoice.setText("Failed to create invoice, select type of contract");
            revealHideNotice(tfWarningCreateInvoice);

            // Lze vypsat hlasku ze se nezdarilo vytvorit Invoice
            // protoze neni vybran typ smlouvy Payer nebo Seller
            return;
        }

        Boolean isPayer = rbRecordSeller.isSelected();

        if (fn.saveInvoice(
                tfRecordName.getText(),
                tfRecordAddress.getText(),
                tfRecordICO.getText(),
                tfRecordDIC.getText(),
                tfRecordBank.getText(),
                tfRecordNote.getText(),
                isPayer,
                recordContacts,
                recordItems,
                tfRecordRecAdd.getText(),
                dpRecordIssuingDate.getEditor().getText(),
                dpRecordBillingDate.getEditor().getText()
        )) {
            tfNotifyCreateInvoice.setText("Invoice was created");
            revealHideNotice(tfNotifyCreateInvoice);

            // Lze vypsat hlasku ze se podarilo vytvorit Invoice
            clearInvoice();
            return;
        }
        tfWarningCreateInvoice.setText("Failed to create invoice");
        revealHideNotice(tfWarningCreateInvoice);

        // Lze vypsat hlasku ze se nezdarilo vytvorit Invoice
    }

    /**
     * Click on Add email.
     */
    @FXML
    private void recordAddEmail() {
        addContact(
                tfRecordAddContactEmail,
                ContactType.EMAIL,
                "email",
                recordContacts
        );
    }

    /**
     * Click on Add telephone.
     */
    @FXML
    private void recordAddTelephone() {
        addContact(
                tfRecordAddContactTelephone,
                ContactType.TELEPHONE,
                "telephone",
                recordContacts
        );
    }

    /**
     * Click on Add item.
     */
    @FXML
    private void recordAddItem() {
        String[] args = new String[5];

        args[0] = tfRecordItemName.getText();
        args[1] = tfRecordItemDesc.getText();
        args[2] = tfRecordItemQuantity.getText();
        args[3] = tfRecordItemUnit.getText();
        args[4] = tfRecordItemPrice.getText();

        if (
                fn.matchPoint(args[0], ItemsType.NAME).equals("") &&
                fn.matchPoint(args[2], ItemsType.QUANTITY).equals("") &&
                fn.matchPoint(args[3], ItemsType.UNIT).equals("") &&
                fn.matchPoint(args[4], ItemsType.PRICE).equals("")
                ) {
            recordItems.add(new ItemTable(
                    args[0],
                    args[1],
                    args[2],
                    args[3],
                    args[4]
            ));
            clearItemFields();
            return;
        }
        tfWarningAddItem.setText("Failed to add content to the table");
        revealHideNotice(tfWarningAddItem);
        // Lze vypsat hlasku ze se nepovedl pridat obsah do tabulky
    }

    /**
     * Click on Add email.
     */
    @FXML
    private void ownerAddEmail() {
        addContact(
                tfOwnerAddContactEmail,
                ContactType.EMAIL,
                "email",
                ownerContacts
        );
    }

    /**
     * Click on Add telephone.
     */
    @FXML
    private void ownerAddTelephone() {
        addContact(
                tfOwnerAddContactTelephone,
                ContactType.TELEPHONE,
                "telephone",
                ownerContacts
        );
    }

    /**
     * Add contact to table.
     * @param tf field
     * @param contact type
     * @param type type string
     */
    private void addContact(TextField tf,
                                 ContactType contact,
                                 String type,
                                 ObservableList<ContactTable> list) {
        String arg = tf.getText();
        if (fn.matchPoint(arg, contact).equals("") &&
                contactNotExist(arg, type, list)) {
            list.add(new ContactTable(type, arg, false));
            tf.setText("");
            return;
        }
        tfWarningAddContact.setText("Failed to add contact");
        revealHideNotice(tfWarningAddContact);

        // Lze vypsat hlasku ze kontakt se nepovedlo pridat
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
     * @param type column
     * @param val column
     * @param del column
     * @param isOwner bool
     */
    private void setContactTable(TableColumn<ContactTable, String> type,
                                 TableColumn<ContactTable, String> val,
                                 TableColumn<ContactTable, ContactTable> del,
                                 Boolean isOwner) {
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        val.setCellValueFactory(new PropertyValueFactory<>("value"));

        setTableListener(del, isOwner);

        if (isOwner) {
            tvOwnerContacts.setItems(ownerContacts);
        } else {
            tvRecordContacts.setItems(recordContacts);
        }
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
            if (contactNotExist(ele, arg, list)) {
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
    private boolean contactNotExist(String val,
                                 String arg,
                                 ObservableList<ContactTable> list) {
        for (ContactTable ele : list) {
            if (ele.getType().equals(arg) && ele.getValue().equals(val)) {
                return false;
            }
        }
        return true;
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

        tfListen(dpToolsFromDate, tfToolsWarnFromDate, DateType.DATE);
        tfListen(dpToolsToDate, tfToolsWarnToDate, DateType.DATE);
    }

    /**
     * Set listener for textField.
     * @param theField field
     * @param theWarn warn
     * @param input type
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

        clearItemFields();

        tfRecordRecAdd.setText("");
        dpRecordBillingDate.getEditor().setText("");
        dpRecordIssuingDate.getEditor().setText("");
    }

    private void clearItemFields() {
        tfRecordItemName.setText("");
        tfRecordItemUnit.setText("");
        tfRecordItemQuantity.setText("");
        tfRecordItemPrice.setText("");
        tfRecordItemDesc.setText("");
    }

    @FXML
    private void selectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(primaryStage);

        if(selectedDirectory == null){
            tfFolderSave.setText("No Directory selected");
        }else{
            tfFolderSave.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void export2PDF() {
        if (!fn.getPDF(tfFolderSave.getText())) {
            tfWarningExport.setText("Failed to create PDF");
            revealHideNotice(tfWarningExport);

            // Nepodarilo se vytvorit PDF
            // Hlaska
            return;
        }
        tfNotifyExport.setText("PDF was successfully created");
        revealHideNotice(tfNotifyExport);

        // Uspesne se povedly vytvorit smlouvy v PDF
    }

    @FXML
    private void summarizeMoney() {
        String result = fn.summarizeMoney(
                dpToolsFromDate.getEditor().getText(),
                dpToolsToDate.getEditor().getText());

        if (result != null) {
            lbShowMoney.setText(result.trim());
            return;
        }

        tfWarningSummarize.setText("Date was not selected");
        revealHideNotice(tfWarningSummarize);

        // Lze vypsat hlasku ze neni co scitat Invoice nebyl vytvoren
    }

    private void revealHideNotice(TextInputControl field){
        field.setVisible(true);
        hideNotice.setNode(field);
        hideNotice.playFromStart();
    }

    private void initTransition(){
        hideNotice = new FadeTransition(Duration.seconds(6));
        hideNotice.setFromValue(1.0);
        hideNotice.setToValue(0.0);
    }

}
