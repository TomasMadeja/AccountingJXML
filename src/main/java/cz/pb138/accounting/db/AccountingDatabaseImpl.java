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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;


import java.io.StringReader;


public class AccountingDatabaseImpl {

    /**
     * Refers to eXist-db
     */
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";

    private static final String COLNAME = "/db/pb138-accountingcollection";
    private static final String MAKECOLNAME = "pb138-accountingcollection";

    private static final String LINUXSTARTUP = "/bin/startup.sh";
    private static final String LINUXSHUTDOWN = "/bin/shutdown.sh";
    private static final String WINDOWSSTARTUP = "\\bin\\startup.bat";
    private static final String WINDOWSSHUTDOWN = "\\bin\\startup.bat";

    private static final String OWNER = "pb138-accowner";
    private static final String EXPENSES = "pb138-accexpenses";
    private static final String EARNINGS = "pb138-accearnings";

    private String username;
    private String password;

    private boolean dbDetected;
    private Database database;
    private Collection col;

    private XMLResource owner;
    private XMLResource expenses;
    private XMLResource earnings;

    private  boolean ownerSet;

    public AccountingDatabaseImpl(String username, String password) throws XMLDBException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        this.username = username;
        this.password = password;
        dbDetected = false;
        ownerSet = false;

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

    public boolean initDatabase(String path) throws XMLDBException {
        return initDatabase(path, -1);
    }

    public boolean initDatabase(String path, long waits) throws XMLDBException {
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

    public boolean killDatabase(String path) {
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

    public boolean colFound() {
        return dbDetected;
    }

    public boolean isOwnerSet() {return ownerSet;}

    private boolean tryInit(String path) throws XMLDBException{
        try {
            dbDetected = initCollection();
        } catch (XMLDBException ex) {
            if (ex.errorCode != ErrorCodes.VENDOR_ERROR  ||
                    "Failed to read server's response: Connection refused (Connection refused)"
                            .compareTo(ex.getMessage()) != 0 ) {
                if (ex.getMessage().contains("not allowed to")) {
                    throw new XMLDBException(ErrorCodes.PERMISSION_DENIED, ex.vendorErrorCode);
                }
                return false;
            }
        }
        return dbDetected;
    }

    private boolean initCollection() throws XMLDBException {
        col = DatabaseManager.getCollection(URI + COLNAME, username, password);

        if (col == null) {
            col = DatabaseManager.getCollection(URI + "/db", username, password);
            ((XPathQueryService)col.getService("XPathQueryService", "1.0"))
                    .query("let $create-collection := xmldb:create-collection(\"/db\", \"" + MAKECOLNAME
                                +"\")\n"
                                + " return $create-collection").clear();
            col.close();
            col = DatabaseManager.getCollection(URI + COLNAME, username, password);
        }

        fillResources();

        return true;
    }


    private void fillResources() throws XMLDBException {
        owner = fillResource(OWNER);
        try {
            validateResource((String) owner.getContent(), OWNER);
            ownerSet = true;
        } catch (XMLDBException ex){
            ownerSet = false;
        }
        expenses = fillResource(EXPENSES);
        validateResource((String) expenses.getContent(), EXPENSES);
        earnings = fillResource(EARNINGS);
        validateResource((String) earnings.getContent(), EARNINGS);
    }

    private XMLResource fillResource(String resourceName) throws XMLDBException {
        XMLResource resource = (XMLResource) col.getResource(resourceName);
        if (resource == null) {
            resource = createRecordResource(resourceName);
        }
        return resource;
    }

    private XMLResource createRecordResource(String resourceName) throws XMLDBException {
        XMLResource resource = (XMLResource) col.createResource(resourceName, "XMLResource");
        resource.setContent("<" + resourceName + ">\n</" + resourceName + ">" );
        col.storeResource(resource);
        return resource;
    }

    public XMLResource getOwner() {
        return owner;
    }

    private void validateResource(String doc, String type) throws XMLDBException {
        try {
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new File(type + ".xsd"));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setSchema(schema);
            dbf.newDocumentBuilder().parse(new InputSource(new StringReader(doc)));
        } catch (SAXException|ParserConfigurationException|IOException ex) {
            throw new XMLDBException(ErrorCodes.UNKNOWN_RESOURCE_TYPE);
        }
    }
}
