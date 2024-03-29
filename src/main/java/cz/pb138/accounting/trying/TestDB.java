package cz.pb138.accounting.trying;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.exist.xmldb.DatabaseInstanceManager;

public class TestDB {
    public static void main(String args[]) throws Exception {
        // initialize driver
        Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
        Database database = (Database)cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        // try to read collection
        Collection col =
                DatabaseManager.getCollection("xmldb:exist:///db", "admin", "");
        String resources[] = col.listResources();
        System.out.println("Resources:");
        for (int i = 0; i < resources.length; i++) {
            System.out.println(resources[i]);
        }

        // shut down the database
        DatabaseInstanceManager manager = (DatabaseInstanceManager)
                col.getService("DatabaseInstanceManager", "1.0");
        manager.shutdown();
    }
}
