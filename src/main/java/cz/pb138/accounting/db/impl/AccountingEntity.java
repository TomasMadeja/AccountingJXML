package cz.pb138.accounting.db.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class AccountingEntity {
    protected Document doc;
    protected Element root;

    public static final String SUFFIX = "xs";

    private final String[] contact;
    private final String[] unique;

    private Map<String, Element> uniqueElements;
    private Map<String, Element> contactRoots;
    private Map<String, List<Element>> contacts;

    public AccountingEntity(Document doc, String[] uniqueS, String[] contactS) {
        this.doc = doc;
        this.contact = contactS;
        this.unique = uniqueS;

        root = doc.createElement("record");
        doc.getDocumentElement().appendChild(root);

        uniqueElements = new HashMap<String, Element>();
        contactRoots = new HashMap<String, Element>();
        contacts = new HashMap<String, List<Element>>();

        Element e;
        for (String u : unique) {
            e = doc.createElement(u);
            root.appendChild(e);
            uniqueElements.put(u, e);
        }

        for (String c : contact) {
            e = doc.createElement(c + SUFFIX);
            root.appendChild(e);
            contactRoots.put(c + SUFFIX, e);
            contacts.put(c, new ArrayList<Element>());
        }
    }

    public AccountingEntity(Document doc, Node rootElement, String[] unique, String[] contact) {
        this.doc = doc;
        this.root = (Element) rootElement;
        this.contact = contact;
        this.unique = unique;

        uniqueElements = new HashMap<String, Element>();
        contactRoots = new HashMap<String, Element>();
        contacts = new HashMap<String, List<Element>>();

        domToDict(root);
    }


    public AccountingEntity addContact(String type, String value) {
        Element croot;
        if ((croot = contactRoots.get(type + SUFFIX)) != null) {
            Element e = doc.createElement(type);
            e.setTextContent(value);
            croot.appendChild(e);
            contacts.get(type).add(e);
        }
        return this;
    }

    public AccountingEntity removeContact(String type, String value) {
        Element croot;
        if ((croot = contactRoots.get(type + SUFFIX)) != null) {
            Iterator<Element> iterator = contacts.get(type).iterator();
            while (iterator.hasNext()) {
                Element ele = iterator.next();

                if (ele.getTextContent().compareTo(value) == 0) {
                    croot.removeChild(ele);
                    iterator.remove();
                }
            }
        }
        return this;
    }


    public AccountingEntity changeValue(String type, String value) throws AccountingException {
        Element e;
        if (( e = uniqueElements.get(type)) != null) {
            e.setTextContent(value);
        }
        return this;
    }


    public AccountingEntity changeValue(String type, String oldValue, String newValue){
        if (contacts.containsKey(type)) {
            List l = contacts.get(type);
            for (Object e : l) {
                if (((Element) e).getTextContent().compareTo(oldValue) == 0) {
                    ((Element) e).setTextContent(newValue);
                }
            }
        }
        return this;
    }


    public String getValue(String type) {
        return uniqueElements.containsKey(type) ? uniqueElements.get(type).getTextContent() : null;
    }


    public String[] getContact(String type) {
        String[] array = null;
        if (contacts.containsKey(type)) {
            array = new String[contacts.get(type).size()];
            int i = 0;
            for (Element e : contacts.get(type)) {
                array[i] = e.getTextContent();
                i++;
            }
        }
        return array;
    }

    protected void delete() {
        if (doc == null) {
            return;
        }

        doc.getDocumentElement().removeChild(root);

        doc = null;
        root = null;
        contactRoots = null;
        contacts = null;
        uniqueElements = null;
    }

    private void domToDict(Element root) {
        for (String u : unique) {
            Element e = (Element) root.getElementsByTagName(u).item(0);
            uniqueElements.put(u, e);
        }

        for (String c : contact) {
            if (!contacts.containsKey(c)) {
                contacts.put(c, new ArrayList<Element>());
            }

            if (!contactRoots.containsKey(c+SUFFIX)) {
                contactRoots.put(c+SUFFIX, (Element) root.getElementsByTagName(c+SUFFIX).item(0));
            }
            Element croot = contactRoots.get(c+SUFFIX);

            NodeList list = croot.getElementsByTagName(c);
            Element e;
            for (int i = 0; i < list.getLength(); i++) {
                e = (Element) list.item(i);
                contacts.get(e.getTagName()).add(e);
            }
        }
    }
}
