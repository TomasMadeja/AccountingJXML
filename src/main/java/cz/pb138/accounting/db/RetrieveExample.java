package cz.pb138.accounting.db;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;
import javax.xml.transform.OutputKeys;
import org.exist.xmldb.EXistResource;

public class RetrieveExample {


    /*
    URI odkazujuce na eXist, needitovat
     */
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";

    /*
    Collection v databaze, mozne vytvorit po spusteni databazy (priklad po spusteni v shelle bin/startup.sh/.bat)
    v  browsery pomocou http://localhost:8080/exist v moznosti collections. /db/ oznacuje root. V pripade ze
    collection neexistuje, tento priklad spadne.
     */
    private static String COLNAME = "/db/testcoll";

    /*
    Testovaci dokument, mozno ho taktiez v http://localhost:8080/exist vytvorit pomocou eXide ako xml subor, musi byt
    ulozeny vo vyssie uvedenej kolekcii. V pripade ze existuje, vypise sa obsah, inak sa vypise "document not found".
     */
    private static String RESNAME = "testdoc";

    public static void main(String args[]) throws Exception {

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection col = null;
        XMLResource res = null;
        try {j
            // get the collection
            col = DatabaseManager.getCollection(URI + COLNAME);
            col.setProperty(OutputKeys.INDENT, "no");
            res = (XMLResource)col.getResource(RESNAME);

            if(res == null) {
                System.out.println("document not found!");
            } else {
                System.out.println(res.getContent());
            }
        } finally {
            //dont forget to clean up!

            if(res != null) {
                try { ((EXistResource)res).freeResources(); } catch(XMLDBException xe) {xe.printStackTrace();}
            }

            if(col != null) {
                try { col.close(); } catch(XMLDBException xe) {xe.printStackTrace();}
            }
        }
    }
}

