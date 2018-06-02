package cz.pb138.accounting.trying;

import cz.pb138.accounting.db.impl.AccountingDatabaseImpl;

public class ADTestMain {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";
    private static final String DIRECTORY = "";


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

//        if (!db.isOwnerSet()) {
//            db.createOwner();
//            db.commitChanges();
//        }

//        db.addExpenditure().addItem("","","","","10")
//                .addItem("","","","","10")
//                .addItem("","","","","10")
//                .changeValue("billing-date", "2006-11-06")
//                .changeValue("issuing-date", "2006-11-07");
//
//        db.addExpenditure().addItem("","","","","6")
//                .addItem("","","","","6")
//                .addItem("","","","","6")
//                .changeValue("billing-date", "2006-11-06")
//                .changeValue("issuing-date", "2006-11-07");

//        db.commitChanges();

        System.out.println(db.ownerRecord());
        System.out.println(db.earningsRecord());
        System.out.println(db.expensesRecord());

//        System.out.println(db.getLossesByIssuingDate("2006-01-01", "2007-01-01"));

//        System.out.println(db.dbAsString());

        db.killDatabase();

        //db.getRecordsBetweenBilling();
//        db.createRecord(true);
//        db.commitChanges();

//        db.killDatabase("/home/adeom/eXist-db");
//        Runtime.getRuntime().exec("/home/adeom/eXist-db/bin/startup.sh");
//        new ProcessBuilder("/bin/bash", "~/eXist-db/bin/startup.sh").start();
    }
}
