package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface HyponymRepository extends CrudRepository<HyponymDAO, Long> {

    /**
     * Find by key phrase
     * 
     * @param kp
     *            The key phrase to search on
     * @return Hyponyms for the key phrase
     */
    @Query(value = "SELECT * FROM hyponym h WHERE h.kp1 = :kp OR h.kp2 = :kp", nativeQuery = true)
    List<HyponymDAO> findByKp(@Param("kp") KeyPhraseDAO kp);

    /**
     * Finds hyponyms by list of key phrases
     * 
     * @param kps
     *            Key phrases to search by
     * @return A list of related hyponyms
     */
    @Query(value = "SELECT * FROM hyponym h WHERE h.kp1 IN :kps OR h.kp2 IN :kps", nativeQuery = true)
    List<HyponymDAO> findByKpIn(@Param("kps") List<KeyPhraseDAO> kps);

}
