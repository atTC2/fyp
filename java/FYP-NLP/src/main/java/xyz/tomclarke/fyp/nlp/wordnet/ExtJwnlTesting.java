package xyz.tomclarke.fyp.nlp.wordnet;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * Testing the extJWNL package - overall it seems easy to setup and to use, so
 * will probably be the way I access WordNet in code from now onwards.
 * 
 * @author tbc452
 *
 */
@Deprecated
public class ExtJwnlTesting {

    private static final Logger log = LogManager.getLogger(ExtJwnlTesting.class);

    public static void main(String[] args) throws JWNLException {
        new ExtJwnlTesting();
    }

    public ExtJwnlTesting() throws JWNLException {
        Dictionary d = Dictionary.getDefaultResourceInstance();

        // Noun - Bed
        IndexWord word = d.getIndexWord(POS.NOUN, "bed");
        printPossibleSynonyms("bed", getPossibleSynonyms(word.getSenses()));

        // Noun - facial recognition
        word = d.getIndexWord(POS.NOUN, "facial recognition");
        printPossibleSynonyms("facial recognition", getPossibleSynonyms(word.getSenses()));

    }

    /**
     * Creates lists of possible synonyms
     * 
     * @param synsets
     *            The result of looking up a word
     * @return A list of possible sets of synonyms
     */
    public List<List<String>> getPossibleSynonyms(List<Synset> synsets) {
        List<List<String>> sets = new ArrayList<List<String>>();

        for (Synset synset : synsets) {
            List<String> synonyms = new ArrayList<String>();
            for (Word word : synset.getWords()) {
                synonyms.add(word.getLemma());
            }
            if (!synonyms.isEmpty()) {
                sets.add(synonyms);
            }
        }

        return sets;
    }

    /**
     * Prints out synonyms
     * 
     * @param word
     *            The original word
     * @param possibleSynonyms
     *            The sets of synonyms
     */
    public void printPossibleSynonyms(String word, List<List<String>> possibleSynonyms) {
        log.info("Original word: " + word);
        for (List<String> synonyms : possibleSynonyms) {
            log.info("Possible synonyms: " + synonyms);
        }
    }

}
