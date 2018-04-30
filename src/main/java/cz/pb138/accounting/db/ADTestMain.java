package cz.pb138.accounting.db;

import cz.pb138.accounting.db.AccountingDatabaseImpl;
import org.apache.commons.lang3.SystemUtils;

public class ADTestMain {

    public static void main(String args[]) throws Exception {
        AccountingDatabaseImpl db = new AccountingDatabaseImpl(null, null);

        if (!db.colFound()) {
            if (db.initDatabase("/home/adeom/eXist-db")) {
                System.out.println("...!!...");
            } else {
                System.out.println("...No...");
            }
        }
        db.killDatabase("/home/adeom/eXist-db");
//        Runtime.getRuntime().exec("/home/adeom/eXist-db/bin/startup.sh");
//        new ProcessBuilder("/bin/bash", "~/eXist-db/bin/startup.sh").start();
    }
}
