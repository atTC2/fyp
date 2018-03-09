package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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

    /**
     * Finds synonym records related to a key phrase
     * 
     * @param kp
     *            The key phrase to find relations of
     * @return Related synonym records
     */
    @Query(value = "SELECT * FROM synonym WHERE kp != :kp AND syn_link IN (SELECT syn_link FROM synonym WHERE kp = :kp)", nativeQuery = true)
    List<SynonymDAO> findRelatedByKp(@Param("kp") KeyPhraseDAO kp);

}
