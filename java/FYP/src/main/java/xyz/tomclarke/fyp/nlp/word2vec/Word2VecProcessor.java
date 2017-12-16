package xyz.tomclarke.fyp.nlp.word2vec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.util.CoreMap;
import xyz.tomclarke.fyp.nlp.paper.Paper;

/**
 * Uses word2vec
 * 
 * @author tbc452
 *
 */
public class Word2VecProcessor {

    /**
     * Loads the Word2Vec Google News pre-processed data
     * 
     * @return Word2Vec The loaded Google News object
     */
    public static Word2Vec loadGoogleNewsVectors() {
        return WordVectorSerializer.readWord2VecModel(new File("/home/tom/FYP/GoogleNews-vectors-negative300.bin.gz"));
    }

    /**
     * Loads the Word2Vec freebase ID
     * 
     * @return Word2Vec The loaded Google News object
     */
    public static Word2Vec loadFreebaseIDVectors() {
        return WordVectorSerializer.readWord2VecModel(new File("/home/tom/FYP/freebase-vectors-skipgram1000.bin.gz"));
    }

    /**
     * Loads the Word2Vec freebase name
     * 
     * @return Word2Vec The loaded Google News object
     */
    public static Word2Vec loadFreebaseNameVectors() {
        return WordVectorSerializer
                .readWord2VecModel(new File("/home/tom/FYP/freebase-vectors-skipgram1000-en.bin.gz"));
    }

    /**
     * Loads the Word2Vec Google News pre-processed data
     * 
     * @return Word2Vec The loaded Google News object
     */
    public static Word2Vec loadWiki2Vec() {
        return WordVectorSerializer.readWord2VecModel(new File("/home/tom/FYP/en_1000_no_stem.tar.gz"));
    }

    /**
     * Takes pre-processed papers and gets all sentences.
     * 
     * @param papers
     *            The papers to get sentences from
     * @return A list of sentences
     */
    private static List<String> createSentencesFile(List<Paper> papers) {
        List<String> sentences = new ArrayList<String>();

        for (Paper paper : papers) {
            if (paper.getAnnotations() != null && !paper.getAnnotations().isEmpty()) {
                for (CoreMap sentence : paper.getAnnotations()) {
                    // Any extra pre-processing, do here
                    sentences.add(sentence.get(TextAnnotation.class).toLowerCase());
                }
            }
        }

        return sentences;
    }

    /**
     * Processes papers with word2vec TODO consider saving this automatically? Maybe
     * a hash of the papers given
     * 
     * @param papers
     *            The papers to process
     * @return The trained Word2Vec instance
     */
    public static Word2Vec generateFromPapers(List<Paper> papers) {
        // Setup the iterator holding the data
        SentenceIterator iter = new CollectionSentenceIterator(createSentencesFile(papers));
        iter.setPreProcessor(new SentencePreProcessor() {
            private static final long serialVersionUID = 8231301166140848124L;

            @Override
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });

        // Setup the tokenizer
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        // Setup the word2vec instance
        Word2Vec vec = new Word2Vec.Builder().minWordFrequency(1).iterations(1).layerSize(100).seed(42).windowSize(5)
                .iterate(iter).tokenizerFactory(t).build();

        vec.fit();

        return vec;
    }
}
