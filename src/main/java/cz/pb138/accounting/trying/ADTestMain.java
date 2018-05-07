package cz.pb138.accounting.trying;

import cz.pb138.accounting.db.AccountingDatabaseImpl;
import org.apache.commons.lang3.SystemUtils;
import org.xmldb.api.DatabaseManager;

public class ADTestMain {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";
    private static final String DIRECTORY = "/Users/martintrubelik/Install/eXist-db";


    public static void main(String args[]) throws Exception {
        AccountingDatabaseImpl db = new AccountingDatabaseImpl(USERNAME, PASSWORD);

//        if (!db.colFound()) {
//            if (db.initDatabase(DIRECTORY)) {
//                if (!db.isOwnerSet()) {
//                    db.createOwner();
//                }
//            } else {
//                System.out.println("...No...");
//            }
//        }

        if (!db.isOwnerSet()) {
            db.createOwner();
            db.commitChanges();
        }

        db.killLocalDB();

        //db.getRecordsBetweenBilling();
//        db.createRecord(true);
//        db.commitChanges();

//        db.killDatabase("/home/adeom/eXist-db");
//        Runtime.getRuntime().exec("/home/adeom/eXist-db/bin/startup.sh");
//        new ProcessBuilder("/bin/bash", "~/eXist-db/bin/startup.sh").start();
    }
}
