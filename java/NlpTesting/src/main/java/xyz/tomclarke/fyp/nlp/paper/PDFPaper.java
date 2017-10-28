package xyz.tomclarke.fyp.nlp.paper;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Loads PDFs in, strips not useful parts and allowed retrieval of text
 * 
 * @author tbc452
 *
 */
public class PDFPaper extends Paper {

    private static final long serialVersionUID = 3727602886028917271L;
    private static final Logger log = LogManager.getLogger(PDFPaper.class);

    public PDFPaper(String fileLocation) {
        super(fileLocation);

        // Text may have been loaded in.
        if (getText() == null) {
            try {
                File pdf = new File(fileLocation);
                PDDocument pdd = PDDocument.load(pdf);
                PDFTextStripper textStripper = new PDFTextStripper();

                setText(textStripper.getText(pdd));

                pdd.close();
            } catch (IOException e) {
                log.error("Problem loading PDF document", e);
            }
        }
    }

}
