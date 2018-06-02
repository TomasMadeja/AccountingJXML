package cz.pb138.accounting.fn;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class AccountingXSLT {
    public static String getHTML(String xml) {
        TransformerFactory tf = TransformerFactory.newInstance();

        Transformer xsltProc = null;

        try {
            xsltProc = tf.newTransformer(
                    new StreamSource(new File("src/main/resources/invoice.xsl")));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

        try {
            assert xsltProc != null;

            StringWriter writer = new StringWriter();

            xsltProc.transform(
                    new StreamSource(new StringReader(xml)),
                    new StreamResult(writer));

            return writer.toString();
        }
        catch (NullPointerException | TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }
}
