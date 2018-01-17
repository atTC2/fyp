package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

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

}
