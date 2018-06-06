package cz.pb138.accounting.trying;

import cz.pb138.accounting.db.Owner;
import cz.pb138.accounting.db.Record;
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

//        Template for fast input, don't change

//        Owner owner = db.getOwner()
//                .changeValue("name","")
//                .changeValue("address","")
//                .changeValue("ico","")
//                .changeValue("dic","")
//                .changeValue("bank-information","")
//                .changeValue("note","")
//                .addContact("email", "")
//                .addContact("telephone", "");
//
//        Record expenditure = db.addExpenditure()
//                .changeValue("billing-date","")
//                .changeValue("issuing-date","")
//                .changeValue("recipient-address","")
//
//                .changeValue("entity-name","")
//                .changeValue("entity-address","")
//                .changeValue("entity-ico","")
//                .changeValue("entity-dic","")
//                .changeValue("entity-bank-information","")
//                .changeValue("entity-note","")
//                .addContact("email", "")
//                .addContact("telephone", "")
//
//                .addItem("","","1","","1");
//
//        Record revenue = db.addRevenue()
//                .changeValue("billing-date","")
//                .changeValue("issuing-date","")
//                .changeValue("recipient-address","")
//
//                .changeValue("entity-name","")
//                .changeValue("entity-address","")
//                .changeValue("entity-ico","")
//                .changeValue("entity-dic","")
//                .changeValue("entity-bank-information","")
//                .changeValue("entity-note","")
//                .addContact("email", "")
//                .addContact("telephone", "")
//
//                .addItem("","","1","","1");;
//
//        db.commitChanges();

        System.out.println(db.ownerRecord());
        System.out.println(db.earningsRecord());
        System.out.println(db.expensesRecord());


        System.out.println("Boom: " + db.getOwner().getValue("name").length());

        System.out.println(db.getLossesByIssuingDate("0000-01-01", "2100-01-01"));
        System.out.println(db.getEarningsByIssuingDate("0000-01-01", "1000-01-01"));

//        System.out.println(db.expenditures());
//        System.out.println(db.revenues());
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
