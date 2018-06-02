package cz.pb138.accounting.trying;

import cz.pb138.accounting.fn.AccountingPDF;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PDFTest {
    public static void main(String[] args) throws IOException {

        new AccountingPDF().savePDF(readFile("/Users/martintrubelik/Downloads/index.html", StandardCharsets.UTF_8), "/Users/martintrubelik/Downloads/output.pdf");

    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
