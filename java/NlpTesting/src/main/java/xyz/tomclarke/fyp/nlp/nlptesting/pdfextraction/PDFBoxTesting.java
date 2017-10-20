package xyz.tomclarke.fyp.nlp.nlptesting.pdfextraction;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * A small class to demonstrate that PDFBox can be useful in text extraction
 * from PDFs
 * 
 * @author tbc452
 *
 */
public class PDFBoxTesting {

    public static void main(String[] args) throws IOException {
        new PDFBoxTesting();
    }

    public PDFBoxTesting() throws IOException {
        File pdf = new File(
                "C:\\Users\\Tom\\OneDrive\\University\\Yr3\\FYP\\Data\\ScienceDirect\\1-s2.0-S1566253516301117-main.pdf");
        PDDocument pdd = PDDocument.load(pdf);
        PDFTextStripper textStripper = new PDFTextStripper();

        // Warning printed as uses log4j (1.something) without configuration but seems to work as expected.
        System.out.println(textStripper.getText(pdd));
    }

}
