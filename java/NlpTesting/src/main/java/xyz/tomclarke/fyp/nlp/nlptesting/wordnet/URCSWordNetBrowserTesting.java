package xyz.tomclarke.fyp.nlp.nlptesting.wordnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.rochester.cs.WordNet.wn.PartOfSpeech;
import edu.rochester.cs.WordNet.wn.Synset;
import edu.rochester.cs.WordNet.wn.WordNetManager;

/**
 * Testing URCS Word Net Browser as a way to access the word net database. It
 * seems OK, but not as easy to use or as useful as the extJWNL implementation.
 * 
 * @author tbc452
 *
 */
public class URCSWordNetBrowserTesting {

    private final String wn_home = "C:\\Users\\Tom\\OneDrive\\University\\Yr3\\FYP\\Code\\resources\\wn-dict";

    public static void main(String[] args) throws IOException {
        new URCSWordNetBrowserTesting();
    }

    public URCSWordNetBrowserTesting() throws IOException {
        // Load the main class
        WordNetManager wn = getWordNet(wn_home);
        // Lookup
        // Only nouns returned
        System.out.println("FACIAL RECOGNITION: " + getSynonymFromLookupIgnoringPOS(wn.lookup("facial recognition")));
        // Only select nouns
        System.out.println("BED: " + getSynonymFromLookup(wn.lookup("bed", PartOfSpeech.NOUN)));
        // Null
        System.out.println("ASD: " + getSynonymFromLookupIgnoringPOS(wn.lookup("asd")));
    }

    /**
     * Create a WordNetManager object
     * 
     * @param wn_home
     *            The path to the WordNet database
     * @return The WordNetManager
     * @throws IOException
     *             If something went wrong
     */
    private WordNetManager getWordNet(String wn_home) throws IOException {
        // Load the main class
        WordNetManager wn = new WordNetManager(wn_home);
        // Fix the issue with listeners (stops NullpointerException)
        wn.addWordNetManagerListener(null);
        wn.removeWordNetManagerListener(null);

        return wn;
    }

    /**
     * Gets a list of synonyms from a WordNet lookup (ignoring POS, as the lookup
     * would have).
     * 
     * @param synsets
     *            The map of synonyms relative to each POS.
     * @return A list of synonyms (they could from a different POS).
     */
    private List<String> getSynonymFromLookupIgnoringPOS(Map<PartOfSpeech, Synset[]> synsets) {
        ArrayList<String> synonyms = new ArrayList<String>();

        for (Map.Entry<PartOfSpeech, Synset[]> synset : synsets.entrySet()) {
            synonyms.addAll(getSynonymFromLookup(synset.getValue()));
        }

        return synonyms;
    }

    /**
     * Gets a list of synonyms from a WordNet lookup.
     * 
     * @param synset
     *            A list of synonyms in Synset[] form.
     * @return A list of synonyms.
     */
    private List<String> getSynonymFromLookup(Synset[] synset) {
        ArrayList<String> synonyms = new ArrayList<String>();

        for (int i = 0; i < synset.length; i++) {
            for (int j = 0; j < synset[i].getWordCount(); j++) {
                String newWord = synset[i].getWords()[j].getWord();
                if (!synonyms.contains(newWord)) {
                    synonyms.add(newWord);
                }
            }
        }

        return synonyms;
    }

}
