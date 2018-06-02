package cz.pb138.accounting.trying;

import cz.pb138.accounting.db.impl.AccountingDatabaseImpl;

public class ADDBTestMain {

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
//        db.createOwner();
//        if (!db.isOwnerSet()) {
//            db.createOwner();
//            db.commitChanges();
//        }

//        db.getOwner().addContact("email", "test@test.tst");
//        db.commitChanges();
//
//        db.addExpenditure().addItem("","","","","10").getItems()[0]
//                .setName("duck").name();

//        db.addExpenditure().addItem("sadsad","adadas","23","l","10");
//
//        db.addRevenue().addItem("qweq","qwe","231","s","30");

//        db.addRevenue().addItem("gveg","qwe","231","s","30").getItems()[0]
//                .setName("quack").delete();
//
//        db.commitChanges();
//
//        System.out.println(db.ownerRecord());
//        System.out.println(db.earningsRecord());
//        System.out.println(db.expensesRecord());
//
//
//        System.out.println("Boom: " + db.getOwner().getValue("name").length());
//
//        System.out.println(db.getLossesByIssuingDate("2000-01-01", "2100-01-01"));
//        System.out.println(db.getEarningsByIssuingDate("2000-01-01", "2100-01-01"));


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
