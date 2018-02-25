package xyz.tomclarke.fyp.nlp.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
     * @param papersList
     *            A file containing locations of papers
     * @param canAttemtAnnRead
     *            Specifies whether the paper object should be saved to disk
     * @param saveUpdatedToDisk
     *            Whether to save information addition to disk
     * @return A list of loaded papers
     */
    public static List<Paper> loadNewPapers(InputStream papersList, boolean canAttemptAnnRead,
            boolean saveUpdatedToDisk) {
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
                papers.addAll(loadNewPapers(paperLocation, canAttemptAnnRead, saveUpdatedToDisk));
            }
        }

        return papers;
    }

    /**
     * Loads papers from a supplied string (plural as string could lead to list of
     * files)
     * 
     * @param paperLocation
     *            The location of the paper
     * @param canAttemptAnnRead
     *            Whether the parse should be written to disk (if possible)
     * @param saveUpdatedToDisk
     *            Whether to save information addition to disk
     * @return A list of papers or null if none can be added
     */
    public static List<Paper> loadNewPapers(String paperLocation, boolean canAttemptAnnRead,
            boolean saveUpdatedToDisk) {
        Paper newPaper = loadNewPaper(paperLocation, canAttemptAnnRead, saveUpdatedToDisk);
        List<Paper> papers = new ArrayList<Paper>();
        if (newPaper != null) {
            return Arrays.asList(newPaper);
        } else {
            // How about if it is a directory (containing many papers)?
            File paperDir = new File(paperLocation);
            if (paperDir.isDirectory()) {
                for (String newLocation : paperDir.list()) {
                    newPaper = loadNewPaper(paperDir.getAbsolutePath() + File.separator + newLocation,
                            canAttemptAnnRead, saveUpdatedToDisk);
                    if (newPaper != null) {
                        papers.add(newPaper);
                    }
                }
            }
        }
        return papers;
    }

    /**
     * Creates a new paper object, subject to location type
     * 
     * @param location
     *            The location of the file
     * @param canAttemtAnnRead
     *            Specifies whether the paper object should be saved to disk
     * @param saveUpdatedToDisk
     *            Whether to save information addition to disk
     * @return The new Paper object, or null if the paper is not supported.
     */
    public static Paper loadNewPaper(String location, boolean canAttemptAnnRead, boolean saveUpdatedToDisk) {
        // Try and pick a type of paper on file extension
        int beginningOfFileExtension = location.lastIndexOf('.');
        if (beginningOfFileExtension >= 0) {
            String fileExtension = location.substring(beginningOfFileExtension);
            switch (fileExtension) {
            case ".txt":
                return new TextPaper(location, canAttemptAnnRead, saveUpdatedToDisk);
            case ".pdf":
                return new PDFPaper(location, saveUpdatedToDisk);
            }
        }

        // TODO support web pages (will come much later in development)

        // No other option (and debug because 'warn' always shows up and is really
        // annoying)
        log.debug("Unsupported paper: " + location);
        return null;
    }

}
