package cz.pb138.accounting;

import cz.pb138.accounting.db.impl.AccountingException;

import cz.pb138.accounting.gui.AccountingGUI;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

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

        // Set window
        primaryStage.setTitle("AccountingJXML");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Set from FXML root stylesheet
//        scene.getStylesheets().add("Style.css");

        primaryStage.setResizable(false);
        primaryStage.show();

        // Close window
        primaryStage.setOnCloseRequest( event ->
        {
            controller.killDatabase();
        });
    }
}
