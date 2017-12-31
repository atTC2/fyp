package xyz.tomclarke.fyp.gui.dao;

import org.springframework.data.repository.CrudRepository;

public interface HyponymRepository extends CrudRepository<Hyponym, Long> {

    // TODO fix the queries in this class
    /**
     * Find by key phrase
     * 
     * @param kp
     *            The key phrase to search on
     * @return Hyponyms for the key phrase
     */
    // @Query("SELECT h FROM hyponym h WHERE h.kp1 = :kp OR h.kp2 = :kp")
    // List<Hyponym> findByKp(@Param("kp") KeyPhrase kp);

    /**
     * Finds hyponyms by list of key phrases
     * 
     * @param kps
     *            Key phrases to search by
     * @return A list of related hyponyms
     */
    // @Query("SELECT h FROM hyponym h WHERE h.kp1 IN :kps OR h.kp2 IN :kps")
    // List<Hyponym> findByKp(@Param("kps") List<KeyPhrase> kps);

}
