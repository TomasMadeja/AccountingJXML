package cz.pb138.accounting.fn;

import com.openhtmltopdf.DOMBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AccountingPDF {

    public static void savePDF(String data, String out)
    {
        OutputStream os = null;

        try {
            os = new FileOutputStream(out);

            try {
                PdfRendererBuilder builder = new PdfRendererBuilder();

                builder.withW3cDocument(html5ParseDocument(data), out);
                builder.toStream(os);
                builder.run();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    os.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static org.w3c.dom.Document html5ParseDocument(String html5) {
        org.jsoup.nodes.Document doc;
        doc = Jsoup.parse(html5);
        return DOMBuilder.jsoup2DOM(doc);
    }
}
