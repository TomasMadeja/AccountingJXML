package cz.pb138.accounting;

import cz.pb138.accounting.db.AccountingDatabaseImpl;
import cz.pb138.accounting.fn.AccountingFnImpl;
import cz.pb138.accounting.gui.UserInterface;

import javax.swing.*;
import java.awt.*;

public class Main {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws Exception {
        // Start server embedded mode
        AccountingDatabaseImpl db = new AccountingDatabaseImpl(USERNAME,PASSWORD);
        AccountingFnImpl fn = new AccountingFnImpl(db);

        // Check existence of owner
        if (!db.isOwnerSet()) {
            db.createOwner();
            db.commitChanges();
        }

        // Start gui
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("AccountingJXML");

            // Set defaults
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                System.out.println("Setting of UI goes wrong: " + ex.getMessage());
            }

            // End on X
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // Build Panel
            try {
                frame.setContentPane(new UserInterface(fn).getPanel1());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Frame size
            Integer width = 900;
            Integer height = 700;
            frame.setMaximumSize(new Dimension(width, height));
            frame.setPreferredSize(new Dimension(width, height));
            frame.setMinimumSize(new Dimension(width, height));

            frame.pack();
            frame.setVisible(true);
        });

        // End server
        db.killDatabase();
    }

}
