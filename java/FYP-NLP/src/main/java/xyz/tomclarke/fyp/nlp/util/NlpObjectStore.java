package xyz.tomclarke.fyp.nlp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility to help store NLP objects for faster usage (no need to rebuild data
 * models).
 * 
 * @author tbc452
 *
 */
public class NlpObjectStore {

    private static final Logger log = LogManager.getLogger(NlpObjectStore.class);
    private static final String nlpObjectPath = "/home/tom/FYP/";

    private NlpObjectStore() {
        // Nothing to do here
    }

    /**
     * Saves an NLP object to the database
     * 
     * @param label
     *            The label to identify the NLP object
     * @param obj
     *            The object to save
     * @throws IOException
     */
    public static void saveNlpObj(String label, Object obj) throws IOException {
        // Get rid of the old file if it's there
        File objFile = getFileFromLabel(label);
        if (objFile.exists()) {
            objFile.delete();
        }

        // Write object to disk
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(objFile));
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * Finds if an NLP object with a given label exists on disk
     * 
     * @param label
     *            The label identifying the NLP object
     * @return Whether it is found on disk and loading can be attempted
     */
    public static boolean checkIfCanLoadNlpObj(String label) {
        return getFileFromLabel(label).exists();
    }

    /**
     * Converts a loaded NLP object from bytes to the object
     * 
     * @param label
     *            The label of the object to load
     * @return The NLP Object to be saved, or null if it couldn't be found or loaded
     */
    public static Object loadNlpObj(String label) {
        File objFile = getFileFromLabel(label);

        // Check it exists
        if (!objFile.exists()) {
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile));
            Object nlpObj = ois.readObject();
            ois.close();
            return nlpObj;
        } catch (ClassNotFoundException | IOException e) {
            log.error("Could not load NLP object: " + label, e);
            // It broke, so try making the object again as if it wasn't found
            return null;
        }
    }

    /**
     * Finds the full path to a NLP object with the label specified
     * 
     * @param label
     *            The label of the NLP object
     * @return The full path name
     */
    private static File getFileFromLabel(String label) {
        return new File(nlpObjectPath + label + ".ser");
    }
}
