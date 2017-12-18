package xyz.tomclarke.fyp.nlp.word2vec;

/**
 * Represents different sources of Word2Vec pretrained models
 * 
 * @author tbc452
 *
 */
public enum Word2VecPretrained {

    GOOGLE_NEWS("/home/tom/FYP/GoogleNews-vectors-negative300.bin.gz"), FREEBASE_IDS(
            "/home/tom/FYP/freebase-vectors-skipgram1000.bin.gz"), FREEBASE_NAMES(
                    "/home/tom/FYP/freebase-vectors-skipgram1000-en.bin.gz"), WIKI2VEC(
                            "/home/tom/FYP/en_1000_no_stem.tar.gz");

    private String location;

    private Word2VecPretrained(String location) {
        this.location = location;
    }

    /**
     * Gets the location of the pretrained data
     * 
     * @return The location on the disk
     */
    public String getLocation() {
        return location;
    }

}
