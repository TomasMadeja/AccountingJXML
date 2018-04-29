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

import java.io.IOException;


public class AccountingDatabaseImpl {

    /**
     * Refers to eXist-db
     */
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static final String COLNAME = "/db/accountingcollectionpb138";
    private static final String MAKECOLNAME = "accountingcollectionpb138";
    private static final String LINUXSTARTUP = "/bin/startup.sh";
    private static final String LINUXSHUTDOWN = "/bin/shutdown.sh";
    private static final String WINDOWSSTARTUP = "\\bin\\startup.bat";
    private static final String WINDOWSSHUTDOWN = "\\bin\\startup.bat";

    private String username;
    private String password;

    private Boolean dbDetected;
    private Database database;
    private Collection col;

    public AccountingDatabaseImpl(String username, String password) throws XMLDBException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
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
            if (ex.errorCode != ErrorCodes.VENDOR_ERROR  ||
                            "Failed to read server's response: Connection refused (Connection refused)"
                                    .compareTo(ex.getMessage()) != 0 ) {
                throw new XMLDBException(ex.errorCode, ex.vendorErrorCode);
            }
        }
    }

    public Boolean initDatabase(String path) throws XMLDBException {
        return initDatabase(path, 60);
    }

    public Boolean initDatabase(String path, long waits) throws XMLDBException {
        if (dbDetected) {
            return false;
        }

        String start;
        if (SystemUtils.IS_OS_WINDOWS) {
            start = WINDOWSSTARTUP;
        } else if (SystemUtils.IS_OS_LINUX) {
            start = LINUXSTARTUP;
        } else {
            return false;
        }

        Process pr = null;
        try {
            pr = Runtime.getRuntime().exec(path + start);
        } catch (IOException ex) {
            return false;
        }

        for (int i = 0; i < waits && !dbDetected; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                pr.destroy();
                return false;
            }
            try {
                dbDetected = initCollection();
            } catch (XMLDBException ex) {
                if (ex.errorCode != ErrorCodes.VENDOR_ERROR  ||
                        "Failed to read server's response: Connection refused (Connection refused)"
                                .compareTo(ex.getMessage()) != 0 ) {
                    try {
                        Thread.sleep(10000);
                        pr.destroy();
                        killDatabase(path);
                    } catch (InterruptedException e) {
                        killDatabase(path);
                        return false;
                    }
                    if (ex.errorCode == ErrorCodes.PERMISSION_DENIED) {
                        throw new XMLDBException(ex.errorCode, ex.vendorErrorCode);
                    }
                    return false;
                }
            }
        }

        if (!dbDetected) {
            try {
                Thread.sleep(10000);
                killDatabase(path);
            } catch (InterruptedException ex) {
                killDatabase(path);
                return false;
            }
        }

        return col != null;
    }

    public Process killDatabase(String path) {
        String kill;
        if (SystemUtils.IS_OS_WINDOWS) {
            kill = WINDOWSSHUTDOWN;
        } else if (SystemUtils.IS_OS_LINUX) {
            kill = LINUXSHUTDOWN;
        } else {
            return null;
        }

        Process pr = null;
        try {
            pr = Runtime.getRuntime().exec(path + kill + " -u "
                    + username + " -p " + password);
        } catch (IOException ex) {
            return null;
        }
        return pr;
    }

    public void updateLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Boolean colFound() {
        return dbDetected;
    }


    private Boolean initCollection() throws XMLDBException {
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
