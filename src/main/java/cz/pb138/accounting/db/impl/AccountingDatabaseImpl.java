package cz.pb138.accounting.db.impl;


import cz.pb138.accounting.db.*;
import org.apache.commons.lang3.SystemUtils;
import org.exist.xmldb.DatabaseInstanceManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;


import java.io.StringReader;

/**
 * Implementation of AccountingDatabase
 * Uses eXistDB, supports both embedded and server mode, constructor expects embedded mode
 *
 * @author Tomas Madeja
 */
public class AccountingDatabaseImpl implements AccountingDatabase {

    /**
     * Refers to eXist-db
     */
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String SERVERTURI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static final String LOCALURI = "xmldb:exist://";

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
            database.setProperty("create-database", "true");

            DatabaseManager.registerDatabase(database);

        } catch (ClassNotFoundException|IllegalAccessException|XMLDBException|InstantiationException ex) {
            throw new AccountingException(ADBErrorCodes.ACCDB_INSTANTIATING_FAILURE, ex.getMessage(), ex);
        }

        col = null;
        try {
            dbDetected = initCollection(LOCALURI);
        } catch (AccountingException ex) {
            if (ex.errorCode != ADBErrorCodes.CONNECTION_ERROR) {
                throw ex;
            }
        }
    }


    private void configureValidation() throws AccountingException {
        try {
            Collection col_valid = DatabaseManager
                    .getCollection(LOCALURI + "/db/system/config/db/", username, password);
            if (col.getResource(OWNER + ".xsd") == null) {
                Resource res = col.createResource(OWNER + ".xsd", "XMLResource");
                res.setContent(new File(getClass()
                        .getClassLoader()
                        .getResource(OWNER + ".xsd")
                        .getFile()
                ));
                col.storeResource(res);
            }
            if (col.getResource(EXPENSES + ".xsd") == null) {
                Resource res = col.createResource(EXPENSES + ".xsd", "XMLResource");
                res.setContent(new File(getClass()
                        .getClassLoader()
                        .getResource(EXPENSES + ".xsd")
                        .getFile()
                ));
                col.storeResource(res);
            }
            if (col.getResource(EARNINGS + ".xsd") == null) {
                Resource res = col.createResource(EARNINGS + ".xsd", "XMLResource");
                res.setContent(new File(getClass()
                        .getClassLoader()
                        .getResource(EARNINGS + ".xsd")
                        .getFile()
                ));
                col.storeResource(res);
            }
            Resource res;
            if ((res = col.getResource("catalog.xml")) == null) {
                res = col.createResource("catalog.xml", "XMLResource");
            }
            res.setContent(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">\n" +
                            "         <uri name=\"http://www.w3.org/2001/XMLSchema\" uri=\"" + OWNER +
                            ".xsd\"/>\n" +
                            "         <uri name=\"http://www.w3.org/2001/XMLSchema\" uri=\"" + EXPENSES +
                            ".xsd\"/>\n" +
                            "         <uri name=\"http://www.w3.org/2001/XMLSchema\" uri=\"" + EARNINGS +
                            ".xsd\"/>\n" +
                            "</catalog> "
            );
            col.storeResource(res);

            if (col_valid.getResource("collection.xconf") == null) {
                res = col_valid.createResource("collection.xconf", "XMLResource");
                res.setContent("<collection xmlns=\"http://exist-db.org/collection-config/1.0\">\n" +
                        "<validation mode=\"auto\">\n" +
                        "<entity-resolver>\n" +
                        "<catalog uri=\"xmldb:exist:///db/pb138-accountingcollection/catalog.xml\"/>\n" +
                        "</entity-resolver>\n" +
                        "</validation>\n" +
                        "</collection>"
                );
                col_valid.storeResource(res);
            } else {
                if (((XQueryService) col_valid.getService("XPathQueryService", "1.0"))
                        .query("for $c in /collection/validation\n" +
                                "return $c").getSize() == 0) {
                    ((XQueryService) col_valid.getService("XPathQueryService", "1.0"))
                            .query("let $c := /collection\n" +
                                    "return update insert <validation mode=\"auto\">" +
                                    "<entity-resolver>" +
                                    "<catalog uri=\"xmldb:exist:///db/pb138-accountingcollection/catalog.xml\"/>" +
                                    "</entity-resolver>" +
                                    "</validation> into $c");
                    int q = 1;
                }
            }
        } catch (XMLDBException ex) {
            throw new AccountingException(ADBErrorCodes.VALIDATION_SETUP_FAILURE , "Error occured" +
                    " while setting up validation", ex);
        }
    }

    private boolean initDatabase(String path) throws AccountingException {
        return initDatabase(path, -1);
    }

    private boolean initDatabase(String path, long waits) throws AccountingException {
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


    public void killDatabase() throws AccountingException {
        try {
            DatabaseInstanceManager manager = (DatabaseInstanceManager)
                    col.getService("DatabaseInstanceManager", "1.0");
            manager.shutdown();
        } catch (XMLDBException ex) {
            throw new AccountingException(ADBErrorCodes.DATABASE_TERMINATION_FAILURE
                    , ex.errorCode
                    , ex.getMessage()
                    , ex);
        }
    }


    private boolean killDatabase(String path) throws AccountingException {
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

    private void updateLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean colFound() { return dbDetected; }

    public boolean isOwnerSet() { return ownerSet; }

    public Owner getOwner() {
        return new AccountingOwner(ownerDoc, ownerDoc.getDocumentElement());
    }

    private Owner createOwner() throws AccountingException {
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


    public void commitChanges() throws AccountingException {
        try {
            owner.setContentAsDOM(ownerDoc);
            expenses.setContentAsDOM(expensesDoc);
            earnings.setContentAsDOM(earningsDoc);

            validateResource((String) owner.getContent(), OWNER);
            validateResource((String) expenses.getContent(), EXPENSES);
            validateResource((String) earnings.getContent(), EARNINGS);

            col.storeResource(expenses);
            col.storeResource(owner);
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

    public void rollback() throws AccountingException {
        try {
            fillResources();
        } catch (AccountingException ex) {
            throw new AccountingException(ex.errorCode, ex.passedErrorCode, "Error occurred while rolling back");
        }
    }

    public Record addRevenue() {
        return createRecord(false);
    }

    public Record addExpenditure() {
        return createRecord(true);
    }

    public List<Record> getRecordsBetweenBilling(String after, String before)
            throws AccountingException{
        return getRecordsBetweenBilling("billing-date", after, before);
    }

    public List<Record> getRecordsBetweenIssuing(String after, String before)
            throws AccountingException {
        return getRecordsBetweenBilling("issuing-date", after, before);
    }

    public InputSource dbAsInputSource() throws AccountingException {
        return new InputSource(new StringReader(dbAsString()));
    }

    public String dbAsString() throws AccountingException{
        try {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (String)
                    ((XPathQueryService) col.getService("XPathQueryService", "1.0"))
                            .query(
                                    "let $a := /" + OWNER + "\n" +
                                            "return " + //"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                            "<root>\n" +
                                            "{\n" +
                                            "for $o in $a\n" +
                                            "return $o\n" +
                                            "}\n" +
                                            "{\n" +
                                            "for $ex in /" + EXPENSES + "\n" +
                                            "return $ex\n" +
                                            "}\n" +
                                            "{\n" +
                                            "for $er in /" + EARNINGS + "\n" +
                                            "return $er\n" +
                                            "}\n" +
                                            "</root>\n"
                            )
                            .getResource(0)
                            .getContent();
        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.vendorErrorCode,
                        "Error connecting while retrieving all records (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage()) || ex.errorCode == ErrorCodes.PERMISSION_DENIED) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.vendorErrorCode,
                        "Error retrieving (read) all records (Missing permissions)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.vendorErrorCode,
                        "Error occurred while retrieving collection (Unknown error)", ex);
            }
        }
    }

    public  double getLossesByIssuingDate(String after, String before) throws AccountingException {
        return sumPrices(EXPENSES, "issuing-date", after, before);
    }

    public  double getEarningsByIssuingDate(String after, String before) throws AccountingException {
        return sumPrices(EARNINGS, "issuing-date", after, before);
    }

    public  double getLossesByBillingDate(String after, String before) throws AccountingException {
        return sumPrices(EXPENSES, "billing-date", after, before);
    }

    public  double getEarningsByBillingDate(String after, String before) throws AccountingException {
        return sumPrices(EARNINGS, "billing-date", after, before);
    }

    public String revenues() throws AccountingException {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>" + ownerRecord()
                + earningsRecord() + "\n</root>";
    }

    public String expenditures() throws AccountingException {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>" + ownerRecord()
                + expensesRecord() + "\n</root>";
    }

    public String ownerRecord() throws AccountingException {
        try {
            return ((String) owner.getContent());
        } catch (XMLDBException ex) {
            throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex);
        }
    }
    public String expensesRecord() throws AccountingException {
        try {
            return ((String) expenses.getContent());
        } catch (XMLDBException ex) {
            throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex);
        }
    }
    public String earningsRecord() throws AccountingException {
        try {
        return ((String) earnings.getContent());
        } catch (XMLDBException ex) {
            throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex);
        }
    }


    private boolean tryInit(String path) throws AccountingException {
        try {
            return (dbDetected = initCollection(SERVERTURI));
        } catch (AccountingException ex) {
            if (ex.errorCode == ADBErrorCodes.CONNECTION_ERROR) {
                return false;
            }
            throw ex;
        }
    }

    private boolean initCollection(String uri) throws AccountingException {
        try {
            col = DatabaseManager.getCollection(uri + COLNAME, username, password);
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
                col = DatabaseManager.getCollection(uri + "/db", username, password);
                ((XPathQueryService) col.getService("XPathQueryService", "1.0"))
                        .query("let $create-collection := xmldb:create-collection(\"/db\", \"" + MAKECOLNAME
                                + "\")\n"
                                + " return $create-collection").clear();
                col.close();
                col = DatabaseManager.getCollection(uri + COLNAME, username, password);
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

        try {
            configureValidation();
        } catch (Exception ex) {
            int q = 1;
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

            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<" + resourceName + " xsi:noNamespaceSchemaLocation=\"" +
                    "xmldb:exist:///db/pb138-accountingcollection/"+ resourceName +
                    ".xsd\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n";

            if (resourceName.compareTo(OWNER) == 0) {
                content += "    <record>\n" +
                        "        <name/>\n" +
                        "        <address/>\n" +
                        "        <ico/>\n" +
                        "        <dic/>\n" +
                        "        <bank-information/>\n" +
                        "        <note/>\n" +
                        "        <telephonexs/>\n" +
                        "        <emailxs/>\n" +
                        "    </record>";
            }

            resource.setContent(content + "</"
                    + resourceName + ">");

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
            ClassLoader classLoader = getClass().getClassLoader();
            Schema schema = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(
                            new File(classLoader
                                    .getResource(type + ".xsd")
                                    .getFile())
                    );

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

    private List<Record> getRecordsBetweenBilling(String name, String after, String before)
            throws AccountingException {
        List<Record> list = new ArrayList<>();
        try {
            ResourceSet result = getBetweenReults(EXPENSES, name, after, before);
            ResourceIterator i = result.getIterator();
            DocumentBuilder docBuild = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder();
            XMLResource res;
            Document resDoc;
            while (i.hasMoreResources()) {
                res = (XMLResource) i.nextResource();
                resDoc = docBuild.parse(new InputSource(new StringReader((String) res.getContent())));
                list.add(new AccountingRecord(resDoc, resDoc.getDocumentElement(), true));
            }

            result = getBetweenReults(EARNINGS, name, after, before);
            i = result.getIterator();
            while (i.hasMoreResources()) {
                res = (XMLResource) i.nextResource();
                resDoc = docBuild.parse(new InputSource(new StringReader((String) res.getContent())));
                list.add(new AccountingRecord(resDoc, resDoc.getDocumentElement(), false));
            }
        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, "Lost connection" +
                        " while retrieving records between " +
                        after + " - " + before + " (Connection error)", ex);
            } else if (isDeniedError(ex.getMessage())) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, "Could not access records" +
                        " while retrieving records between " +
                        after + " - " + before + " (Permission error)", ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, "Unexpected error occured " +
                        " while retrieving records between " +
                        after + " - " + before, ex);
            }
        } catch (ParserConfigurationException|IOException|SAXException ex) {
            throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, "Unexpected error occured " +
                    " while processing records between " +
                    after + " - " + before, ex);
        }

        return list;
    }

    private ResourceSet getBetweenReults(String type, String name, String after, String before)
            throws XMLDBException {
        return ((XPathQueryService) col.getService("XPathQueryService", "1.0"))
                .query("for $r in /" + type + "/record\n" +
                        "where $r/" + name + " >= \'" + after + "\' and " +
                        "$r/" + name + " <= \'" + before + "\'\n" +
                        "return $r");
    }

    private Record createRecord(boolean expense) {
        Document doc = earningsDoc;
        if (expense) {
            doc = expensesDoc;
        }
        return new AccountingRecord(doc, expense);
    }

    private boolean isDCError(String ex) {
        return ex.contains("Connection refused");
    }

    private boolean isDeniedError(String ex) {
        return ex.contains("not allowed to");
    }


    private double sumPrices(String type, String name, String after, String before) throws AccountingException{
        try {
            double r = 0;
            ResourceIterator i = getBetweenSumPrice(type, name, after, before).getIterator();
            while (i.hasMoreResources()) {
                r += Double.parseDouble((String) i.nextResource().getContent());
            }
            return r;
        } catch (XMLDBException ex) {
            if (isDCError(ex.getMessage()) || ex.errorCode == ErrorCodes.COLLECTION_CLOSED) {
                throw new AccountingException(ADBErrorCodes.CONNECTION_ERROR, ex.errorCode, ex.getMessage(), ex);
            } else if (isDeniedError(ex.getMessage()) || ex.errorCode == ErrorCodes.PERMISSION_DENIED) {
                throw new AccountingException(ADBErrorCodes.ACCESS_ERROR, ex.errorCode, ex.getMessage(), ex);
            } else {
                throw new AccountingException(ADBErrorCodes.UNKNOWN_ERROR, ex.errorCode, ex.getMessage(), ex);
            }
        }
    }

    private ResourceSet getBetweenSumPrice(String type, String name, String after, String before)
            throws XMLDBException {
        return ((XPathQueryService) col.getService("XPathQueryService", "1.0"))
                .query("for $r in /" + type + "/record\n" +
                        "where $r/" + name + " >= \'" + after + "\' and " +
                        "$r/" + name + " <= \'" + before + "\'\n" +
                        "return fn:sum($r/itemxs/item/price)");
    }
}
