package cz.pb138.accounting.db;

import cz.pb138.accounting.db.AccountingDatabaseImpl;
import org.apache.commons.lang3.SystemUtils;

public class ADTestMain {

    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final String DIRECTORY = "";


    public static void main(String args[]) throws Exception {
        AccountingDatabaseImpl db = new AccountingDatabaseImpl(USERNAME, PASSWORD);

        if (!db.colFound()) {
            if (db.initDatabase(DIRECTORY)) {
                System.out.println(db.getOwner());
            } else {
                System.out.println("...No...");
            }
        }
//        db.killDatabase("/home/adeom/eXist-db");
//        Runtime.getRuntime().exec("/home/adeom/eXist-db/bin/startup.sh");
//        new ProcessBuilder("/bin/bash", "~/eXist-db/bin/startup.sh").start();
    }
}
