package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SynonymRepository extends CrudRepository<SynonymDAO, Long> {

    /**
     * Find by key phrase
     * 
     * @param kp
     *            The key phrase to search on
     * @return Synonyms for the key phrase
     */
    List<SynonymDAO> findByKp(KeyPhraseDAO kp);

    /**
     * Finds synonyms by list of key phrases
     * 
     * @param kps
     *            Key phrases to search by
     * @return A list of related synonyms
     */
    List<SynonymDAO> findByKpIn(List<KeyPhraseDAO> kps);

}
