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

    private Document ownerDoc;
    private Document expensesDoc;
    private Document earningsDoc;

    private  boolean ownerSet;

    public AccountingDatabaseImpl(String username, String password) throws AccountingException {
        this.username = username;
        this.password = password;
        dbDetected = false;
        ownerSet = false;

        try {
            Class cl = Class.forName(DRIVER);
            database = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(database);
        } catch (ClassNotFoundException|IllegalAccessException|XMLDBException|InstantiationException ex) {
            throw new AccountingException(ADBErrorCodes.ACCDB_INSTANTIATING_FAILURE, ex.getMessage(), ex);
        }

        col = null;
        try {
            dbDetected = initCollection();
        } catch (AccountingException ex) {
            if (ex.errorCode != ADBErrorCodes.CONNECTION_ERROR) {
                throw ex;
            }
        }
    }

    public boolean initDatabase(String path) throws AccountingException {
        return initDatabase(path, -1);
    }

    public boolean initDatabase(String path, long waits) throws AccountingException {
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
            throw new AccountingException(ADBErrorCodes.DATABASE_INITIALIZATION_FAILURE,
                    ex.getMessage(), ex);
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

    public boolean killDatabase(String path) throws AccountingException {
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
            throw new AccountingException(ADBErrorCodes.DATABASE_TERMINATION_FAILURE, ex.getMessage(), ex);
        }
        return true;
    }


    private boolean tryInit(String path) throws AccountingException {
        try {
            return (dbDetected = initCollection());
        } catch (AccountingException ex) {
            if (ex.errorCode == ADBErrorCodes.CONNECTION_ERROR) {
                return false;
            }
            throw ex;
        }
    }

    private boolean initCollection() throws AccountingException {
        try {
            col = DatabaseManager.getCollection(URI + COLNAME, username, password);
        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.vendorErrorCode,
                        "Error connecting while retrieving collection (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.vendorErrorCode,
                        "Error retrieving (read) collection (Missing permissions)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.vendorErrorCode,
                        "Error occurred while retrieving collection (Unknown error)", ex);
            }
        }

        try {
            if (col == null) {
                col = DatabaseManager.getCollection(URI + "/db", username, password);
                ((XPathQueryService) col.getService("XPathQueryService", "1.0"))
                        .query("let $create-collection := xmldb:create-collection(\"/db\", \"" + MAKECOLNAME
                                + "\")\n"
                                + " return $create-collection").clear();
                col.close();
                col = DatabaseManager.getCollection(URI + COLNAME, username, password);
            }
        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.vendorErrorCode,
                        "Error connecting while trying to create Accounting collection (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.vendorErrorCode,
                        "Error creating (write) Accounting collection (Missing permissions)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.vendorErrorCode,
                        "Error occurred while creating Accounting collection (Unknown error)", ex);
            }
        }

        fillResources();

        return true;
    }


    private void fillResources() throws AccountingException {
        try {
            owner = fillResource(OWNER);
            try {
                ownerDoc = validateResource((String) owner.getContent(), OWNER);
                ownerSet = true;
            } catch (AccountingException ex) {
                if (ex.errorCode != ADBErrorCodes.XML_PARSING_ERROR) {
                    throw new AccountingException(ex.errorCode, ex.passedErrorCode, ex.getMessage(), ex);
                }
                ownerSet = false;
            }
            expenses = fillResource(EXPENSES);
            expensesDoc = validateResource((String) expenses.getContent(), EXPENSES);
            earnings = fillResource(EARNINGS);
            earningsDoc = validateResource((String) earnings.getContent(), EARNINGS);

        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.vendorErrorCode,
                        "Error connecting while retrieving resource content (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.vendorErrorCode,
                        "Error retrieving (read) resource content (Missing permissions)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.vendorErrorCode,
                        "Error occurred while retrieving resource content (Unknown error)", ex);
            }
        }
    }

    private XMLResource fillResource(String resourceName) throws AccountingException {
        try {
            XMLResource resource = (XMLResource) col.getResource(resourceName);
            if (resource == null) {
                resource = createRecordResource(resourceName);
            }
            return resource;

        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.vendorErrorCode,
                        "Error connecting while retrieving resource " + resourceName +
                        " (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.vendorErrorCode,
                        "Error accessing resource " + resourceName + " (Missing permissions)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.vendorErrorCode,
                        "Error occurred while retrieving resource " + resourceName +
                        " (Unknown error)", ex);
            }
        }
    }

    private XMLResource createRecordResource(String resourceName) throws AccountingException {
        try {
            XMLResource resource = (XMLResource) col.createResource(resourceName, "XMLResource");
            resource.setContent("<" + resourceName + ">\n</" + resourceName + ">");
            col.storeResource(resource);
            return resource;

        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.vendorErrorCode,
                        "Error connecting while creating resource " + resourceName, ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.vendorErrorCode,
                        "Error creating resource " + resourceName + " (Missing permissions)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.vendorErrorCode,
                        "Error occurred while creating resource " + resourceName, ex);
            }
        }
    }


    private Document validateResource(String doc, String type) throws AccountingException {
        try {
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new File(type + ".xsd"));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setSchema(schema);
            DocumentBuilder docBuild = dbf.newDocumentBuilder();
            docBuild.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException ex) throws SAXException {
                    throw ex;
                }

                @Override
                public void fatalError(SAXParseException ex) throws SAXException {
                    throw ex;
                }

                @Override
                public void warning(SAXParseException ex) throws SAXException {
                    throw ex;
                }
            });
            return docBuild.parse(new InputSource(new StringReader(doc)));

        } catch (SAXException ex) {
            throw new AccountingException(ADBErrorCodes.XML_PARSING_ERROR, "Error parsing " + type +
                    ", invalid format", ex);
        } catch (ParserConfigurationException ex) {
            throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, "Error occurred while attempting to parse " +
                    type, ex);
        } catch (IOException ex) {
            throw new AccountingException(ADBErrorCodes.XSD_READING_ERROR, "Unable to read " + type +
                    ".xsd", ex);
        }
    }


    private boolean isDCError(String ex) {
        return ex.contains("Connection refused");
    }

    private boolean isDeniedError(String ex) {
        return ex.contains("not allowed to");
    }

    public void updateLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean colFound() { return dbDetected; }

    public boolean isOwnerSet() { return ownerSet; }

    public AccountingOwner getOwner() {
        return new AccountingOwner(ownerDoc, ownerDoc.getDocumentElement());
    }

    public AccountingOwner createOwner() throws AccountingException {
        try {
            if (owner != null) {
                col.removeResource(owner);
            }

            owner = createRecordResource(OWNER);

            ownerDoc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader((String) owner.getContent())));
            new AccountingOwner(ownerDoc);
            owner.setContentAsDOM(ownerDoc);
            col.storeResource(owner);
        } catch (ParserConfigurationException|IOException|SAXException ex) {
            throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR,
                    "Error occured while creating owner ()", ex);

        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR,
                        "Unable to connect while creating resource (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR,
                        "Unable to create resource (Permission error)", ex);
            }
        }

        return new AccountingOwner(ownerDoc, ownerDoc.getDocumentElement());
    }

    public AccountingRecord createRecord(boolean expense) {
        Document doc = earningsDoc;
        if (expense) {
            doc = expensesDoc;
        }
        return new AccountingRecord(doc, expense);
    }

    public void commitChanges() throws AccountingException {
        try {
            owner.setContentAsDOM(ownerDoc);
            col.storeResource(owner);
            expenses.setContentAsDOM(expensesDoc);
            col.storeResource(expenses);
            earnings.setContentAsDOM(earningsDoc);
            col.storeResource(earnings);
        } catch (XMLDBException ex) {
            if (ex.errorCode == ErrorCodes.WRONG_CONTENT_TYPE ||
                    ex.errorCode == ErrorCodes.INVALID_RESOURCE) {
                throw new AccountingException(ADBErrorCodes.RESOURCE_COMMIT_FAILURE, ex.getMessage(), ex);
            } else if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR,
                        "Unable to connect while committing changes in resources (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR,
                        "Unable to commit resource changes (Permission error)", ex);
            }
        }
    }


}
