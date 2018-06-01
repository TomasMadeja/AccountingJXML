package cz.pb138.accounting.db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Class representing owner document (as a record)
 *
 * @author Tomas Madeja
 */
public class AccountingOwner {
    private Document doc;

    public final static String[] CONTACT = {"telephone", "email"};
    public final static String[] UNIQUE = {"name", "address", "ico", "dic", "bank-information", "note"};

    private Map<String, Element> uniqueElements;
    private Map<String, List<Element>> contacts;

    /**
     * Creates new owner for document
     * @param doc parent document
     */
    public AccountingOwner(Document doc) {
        this.doc = doc;
        Element root = doc.getDocumentElement();
        Element e;
        for (String u : UNIQUE) {
            e = doc.createElement(u);
            root.appendChild(e);
        }
    }

    /**
     * Represents existing owner
     * @param doc parent document
     * @param recordNode root node of owner doc
     */
    public AccountingOwner(Document doc, Node recordNode) {
        this.doc = doc;
        uniqueElements = new HashMap<String, Element>();
        contacts = new HashMap<String, List<Element>>();

        domToDict((Element)recordNode);
    }


    /**
     * Creates new contact (email, telephone)
     * @param name name representing type of conact (email, telephone)
     * @param value contact information
     * @return returns this object, allows chaining
     */
    public AccountingOwner addContact(String name, String value) {
        for (String matched : CONTACT) {
            if (name.compareTo(matched) == 0) {
                Element e = doc.createElement(matched);
                e.setTextContent(value);
                doc.getDocumentElement().appendChild(e);
            }
        }
        return this;
    }

    /**
     * Changes value of unique element
     * @param name name of specified element
     * @param value new value of element
     * @return returns this object, allows chaining
     * @throws AccountingException correpsonding error code
     */
    public AccountingOwner changeValue(String name, String value) {
        Element e;
        if (( e = uniqueElements.get(name)) != null) {
            e.setTextContent(value);
        }
        return this;
    }

    /**
     * Changes value of contact
     * @param name name of contact
     * @param oldValue previous value to be replaced
     * @param newValue new value
     */
    public AccountingOwner changeValue(String name, String oldValue, String newValue){
        List l;
        if (contacts.containsKey(name)) {
            l = contacts.get(name);
            for (Object e :l) {
                if (((Element) e).getTextContent().compareTo(oldValue) == 0) {
                    ((Element) e).setTextContent(newValue);
                }
            }
        }
        return this;
    }

    /**
     * FInd value of unique element
     * @param name name of the searched element
     * @return String containing value, null if not found
     */
    public String getValue(String name) {
        return uniqueElements.containsKey(name) ? uniqueElements.get(name).getTextContent() : null;
    }

    /**
     * Finds all contacts of certain type
     * @param name contact type name
     * @return Array of contact values
     */
    public String[] getContact(String name) {
        String[] array = null;
        if (contacts.containsKey(name)) {
            array = new String[contacts.get(name).size()];
            int i = 0;
            for (Element e : contacts.get(name)) {
                array[i] = e.getTextContent();
                i++;
            }
        }
        return array;
    }

    /**
     * Removes specified contact
     * @param name contact type name
     * @param value contact value
     * @return returns this object, allows chaining
     */
    public AccountingOwner removeContact(String name, String value) {
        if (contacts.containsKey(name)) {
            Iterator<Element> iterator = contacts.get(name).iterator();
            while (iterator.hasNext()) {
                Element ele = iterator.next();

                if (ele.getTextContent().compareTo(value) == 0) {
                    ele.getParentNode().removeChild(
                            ele
                    );

                    iterator.remove();
                }
            }
        }
        return this;
    }

    private void domToDict(Element root) {
        for (String u : UNIQUE) {
            Element e = (Element) root.getElementsByTagName(u).item(0);
            uniqueElements.put(u, e);
        }

        for (String c : CONTACT) {
            if (!contacts.containsKey(c)) {
                contacts.put(c, new ArrayList<Element>());
            }
            NodeList list = root.getElementsByTagName(c);
            Element e;
            for (int i = 0; i < list.getLength(); i++) {
                e = (Element) list.item(i);
                contacts.get(e.getTagName()).add(e);
            }
        }
    }
}
