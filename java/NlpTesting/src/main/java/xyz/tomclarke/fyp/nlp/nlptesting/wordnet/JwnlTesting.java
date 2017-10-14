package xyz.tomclarke.fyp.nlp.nlptesting.wordnet;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;

/**
 * Testing JWNL - given I can't get it to initialise and given the extended
 * version works (it also comes in Maven making it easy to setup) there's not
 * much point wasting much time on this version trying to make it run.
 * 
 * @author tbc452
 *
 */
public class JwnlTesting {

    public static void main(String[] args) throws JWNLException {
        new JwnlTesting();
    }

    public JwnlTesting() throws JWNLException {
        // Problems doing this:
        /*
         * Exception in thread "main" net.didion.jwnl.JWNLException: Unable to install
         * net.didion.jwnl.dictionary.FileBackedDictionary at
         * net.didion.jwnl.util.factory.Element.install(Element.java:34) at
         * net.didion.jwnl.JWNL.initialize(JWNL.java:169) at
         * xyz.tomclarke.fyp.nlp.NlpTesting.JwnlTesting.<init>(JwnlTesting.java:13) at
         * xyz.tomclarke.fyp.nlp.NlpTesting.JwnlTesting.main(JwnlTesting.java:9) Caused
         * by: java.lang.NullPointerException at
         * net.didion.jwnl.dictionary.FileBackedDictionary.install(FileBackedDictionary.
         * java:111) at net.didion.jwnl.util.factory.Element.install(Element.java:32)
         * ... 3 more
         */
        JWNL.initialize(getClass().getClassLoader().getResourceAsStream("jwnl.xml"));
    }
}
