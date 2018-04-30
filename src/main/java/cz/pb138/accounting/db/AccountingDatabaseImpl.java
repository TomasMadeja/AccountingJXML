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

    private static final String COLNAME = "/db/accountingcollectionpb13";
    private static final String MAKECOLNAME = "accountingcollectionpb13";

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
        return initDatabase(path, -1);
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

        try {
            Runtime.getRuntime().exec(path + start);
        } catch (IOException ex) {
            return false;
        }

        while (!tryInit(path) && waits != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                return false;
            }
            waits--;
        }

        return col != null;
    }

    public Boolean killDatabase(String path) {
        String kill;
        if (SystemUtils.IS_OS_WINDOWS) {
            kill = WINDOWSSHUTDOWN;
        } else if (SystemUtils.IS_OS_LINUX) {
            kill = LINUXSHUTDOWN;
        } else {
            return false;
        }

        try {
            Runtime.getRuntime().exec(path + kill + " -u "
                    + username + " -p " + password);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public void updateLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Boolean colFound() {
        return dbDetected;
    }

    private Boolean tryInit(String path) throws XMLDBException{
        try {
            dbDetected = initCollection();
        } catch (XMLDBException ex) {
            if (ex.errorCode != ErrorCodes.VENDOR_ERROR  ||
                    "Failed to read server's response: Connection refused (Connection refused)"
                            .compareTo(ex.getMessage()) != 0 ) {
                if (ex.getMessage().contains("not allowed to write to collection")) {
                    throw new XMLDBException(ErrorCodes.PERMISSION_DENIED, ex.vendorErrorCode);
                }
                return false;
            }
        }
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
