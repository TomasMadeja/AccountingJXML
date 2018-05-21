package cz.pb138.accounting;

import cz.pb138.accounting.db.AccountingDatabaseImpl;
import cz.pb138.accounting.db.AccountingException;
import cz.pb138.accounting.fn.AccountingFnImpl;

import cz.pb138.accounting.gui.AccountingGUI;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // Set server
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";

    private AccountingDatabaseImpl db;

    public Main() throws AccountingException {
        // Start server embedded mode
        db = new AccountingDatabaseImpl(USERNAME,PASSWORD);

        // Check existence of owner
        if (!db.isOwnerSet()) {
            db.createOwner();
            db.commitChanges();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, AccountingException  {
        // Get JavaFX
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/AccountingGUI.fxml"));
        Parent root = fxml.load();

        // Set fn to controller
        AccountingGUI controller = fxml.<AccountingGUI>getController();
        controller.setFnObject(db);

        // Set window
        primaryStage.setTitle("AccountingJXML");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        // Close window
        primaryStage.setOnCloseRequest( event ->
        {
            try {
                db.killDatabase();
            } catch (AccountingException e) {
                e.printStackTrace();
            }
        });
    }
}
