package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KeyPhraseRepository extends CrudRepository<KeyPhraseDAO, Long> {

    /**
     * Finds key phrases by paper
     * 
     * @param paper
     *            The paper to find key phrases for
     * @return The key phrases for the given paper
     */
    List<KeyPhraseDAO> findByPaper(PaperDAO paper);

    /**
     * Finds key phrases with a given classification
     * 
     * @param classification
     *            The classification to find
     * @return A list of key phrases with the given classification
     */
    List<KeyPhraseDAO> findByClassification(String classification);

    /**
     * Selects a single key phrase row by paper and relative ID
     * 
     * @param paper
     *            The paper the key phrase must be linked to
     * @param relativeId
     *            The relative ID the key phrase must have
     * @return The key phrase row
     */
    KeyPhraseDAO findByPaperAndRelativeId(PaperDAO paper, Long relativeId);

    /**
     * Counts the number of key phrases connected to a paper
     * 
     * @param paper
     *            The paper the key phrases need to be connected to
     * @return The number of key phrases for the given paper
     */
    Long countByPaper(PaperDAO paper);

    /**
     * Counts the number of key phrases with the given classification
     * 
     * @param classification
     *            The classification to filter by
     * @return The number of key phrases with the given classification
     */
    Long countByClassification(String classification);

    /**
     * Finds key phrases for the paper containing the given regex
     * 
     * @param paper
     *            The paper to look for key phrases of
     * @param regex
     *            The regex to check the texts for
     * @return A list of matching key phrases
     */
    @Query(value = "SELECT * FROM key_phrase WHERE paper = :paper AND text regexp :regex", nativeQuery = true)
    List<KeyPhraseDAO> findByPaperAndText(@Param("paper") PaperDAO paper, @Param("regex") String regex);

    /**
     * Finds key phrases for the paper containing the given regex
     * 
     * @param paper
     *            The paper to look for key phrases of
     * @param regex
     *            The regex to check the texts for
     * @param clazz
     *            The classification the user is looking for
     * @return A list of matching key phrases
     */
    @Query(value = "SELECT * FROM key_phrase WHERE paper = :paper AND text regexp :regex AND classification regexp :clazz", nativeQuery = true)
    List<KeyPhraseDAO> findByPaperAndTextAndClassification(@Param("paper") PaperDAO paper, @Param("regex") String regex,
            @Param("clazz") String clazz);

    /**
     * Finds key phrases for the given regex
     * 
     * @param regex
     *            The regex to check the texts for
     * @return A list of matching key phrases
     */
    @Query(value = "SELECT * FROM key_phrase WHERE text regexp :regex", nativeQuery = true)
    List<KeyPhraseDAO> findByPaperAndText(@Param("regex") String regex);

    /**
     * Finds key phrases for the given regex
     * 
     * @param regex
     *            The regex to check the texts for
     * @param clazz
     *            The classification the user is looking for
     * @return A list of matching key phrases
     */
    @Query(value = "SELECT * FROM key_phrase WHERE text regexp :regex AND classification regexp :clazz", nativeQuery = true)
    List<KeyPhraseDAO> findByPaperAndTextAndClassification(@Param("regex") String regex, @Param("clazz") String clazz);

}
