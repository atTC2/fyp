package xyz.tomclarke.fyp.nlp.word2vec;

import java.util.ArrayList;
import java.util.List;

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
	 * Takes pre-processed papers and gets all sentences.
	 * 
	 * @param papers
	 *            The papers to get sentences from
	 * @return A list of sentences
	 */
	private static List<String> createSentencesFile(List<Paper> papers) {
		List<String> sentences = new ArrayList<String>();

		for (Paper paper : papers) {
			if (paper.getCoreNLPAnnotations() != null && !paper.getCoreNLPAnnotations().isEmpty()) {
				for (CoreMap sentence : paper.getCoreNLPAnnotations()) {
					// Any extra pre-processing, do here
					sentences.add(sentence.get(TextAnnotation.class).toLowerCase());
				}
			}
		}

		return sentences;
	}

	/**
	 * Processes papers with word2vec
	 * 
	 * @param papers
	 *            The papers to process
	 * @return The trained Word2Vec instance
	 */
	@SuppressWarnings("serial")
	public static Word2Vec process(List<Paper> papers) {
		// System.setProperty("java.library.path", "");

		// Setup the iterator holding the data
		SentenceIterator iter = new CollectionSentenceIterator(createSentencesFile(papers));
		iter.setPreProcessor(new SentencePreProcessor() {
			@Override
			public String preProcess(String sentence) {
				return sentence.toLowerCase();
			}
		});

		// Setup the tokenizer
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());

		// Setup the word2vec instance
		Word2Vec vec = new Word2Vec.Builder().minWordFrequency(5).iterations(1).layerSize(100).seed(42).windowSize(5)
				.iterate(iter).tokenizerFactory(t).build();

		vec.fit();

		return vec;
	}
}
