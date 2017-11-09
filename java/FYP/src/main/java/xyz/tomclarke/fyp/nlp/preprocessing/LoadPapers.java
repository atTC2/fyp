package xyz.tomclarke.fyp.nlp.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.tomclarke.fyp.nlp.paper.PDFPaper;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.TextPaper;

/**
 * A class which holds utility functions to read in papers.
 * 
 * @author tbc452
 *
 */
public class LoadPapers {

    private static final Logger log = LogManager.getLogger(LoadPapers.class);

    /**
     * Loads a series of papers from resources/papers.txt
     * 
     * @param papersList A file containing locations of papers
     * @return A list of loaded papers
     */
    public static List<Paper> loadNewPapers(File papersList) {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        // Read through each line and try to load in the file.
        try (Scanner scanner = new Scanner(papersList)) {
            while (scanner.hasNextLine()) {
                // Get the next location to process
                String paperLocation = scanner.nextLine();

                // Ensure it isn't a commented out line.
                if (paperLocation.charAt(0) == '#') {
                    continue;
                }

                // Try a single file
                Paper newPaper = loadNewPaper(paperLocation);
                if (newPaper != null) {
                    papers.add(newPaper);
                } else {
                    // How about if it is a directory (containing many papers)?
                    File paperDir = new File(paperLocation);
                    if (paperDir.isDirectory()) {
                        for (String newLocation : paperDir.list()) {
                            newPaper = loadNewPaper(paperDir.getAbsolutePath() + File.separator + newLocation);
                            if (newPaper != null) {
                                papers.add(newPaper);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Problem reading papers.txt", e);
        }

        return papers;
    }

    /**
     * Creates a new paper object, subject to location type
     * 
     * @param location
     *            The location of the file
     * @return The new Paper object, or null if the paper is not supported.
     */
    public static Paper loadNewPaper(String location) {
        // Try and pick a type of paper on file extension
        int beginningOfFileExtension = location.lastIndexOf('.');
        if (beginningOfFileExtension >= 0) {
            String fileExtension = location.substring(beginningOfFileExtension);
            switch (fileExtension) {
            case ".txt":
                return new TextPaper(location);
            case ".pdf":
                return new PDFPaper(location);
            }
        }

        // TODO support web pages (will come much later in development)

        // No other option
        log.warn("Unsupported paper: " + location);
        return null;
    }

}
