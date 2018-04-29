package cz.pb138.accounting.db;

import org.apache.commons.lang3.SystemUtils;
import org.exist.xquery.Except;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;
import javax.xml.transform.OutputKeys;
import org.exist.xmldb.EXistResource;
import org.apache.commons.lang3.SystemUtils;
import org.apache.xmlrpc.XmlRpcException;


public class AccountingDatabaseImpl {

    /**
     * Refers to eXist-db
     */
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static final String COLNAME = "/db/accountingcollectionpb138";
    private static final String MAKECOLNAME = "accountingcollectionpb138";

    private String username;
    private String password;

    private Boolean dbDetected;
    private Database database;
    Collection col;

    AccountingDatabaseImpl(String username, String password) throws Exception {
        this.username = username;
        this.password = password;
        dbDetected = false;

        Class cl = Class.forName(DRIVER);
        database = (Database) cl.newInstance();
        DatabaseManager.registerDatabase(database);

        col = null;
        try {
            dbDetected = initCollection();
        } catch (XMLDBException ex) {
            if (
                    ( ex.errorCode == ErrorCodes.VENDOR_ERROR  &&
                            equals(ex.getMessage(), toString("Failed to read server's response: " +
                                    "Connection refused (Connection refused))")) ) )
        }



    }

    Boolean initCollection() throws XMLDBException {
        col = DatabaseManager.getCollection(URI + COLNAME, username, password);

        if (col == null) {
            col = DatabaseManager.getCollection(URI + "/db", username, password);
            XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "1.0");
            ResourceSet result =
                    xpqs.query("let $create-collection := xmldb:create-collection(\"/db\", \"" + MAKECOLNAME
                            +"\")\n"
                            + " return $create-collection");
            result.clear();
            col.close();
            col = DatabaseManager.getCollection(URI + COLNAME, username, password);
        }

        return true;
    }

}
