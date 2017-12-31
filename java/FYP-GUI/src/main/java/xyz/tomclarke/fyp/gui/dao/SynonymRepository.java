package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SynonymRepository extends CrudRepository<Synonym, Long> {

    /**
     * Find by key phrase
     * 
     * @param kp
     *            The key phrase to search on
     * @return Synonyms for the key phrase
     */
    List<Synonym> findByKp(KeyPhrase kp);

    /**
     * Finds synonyms by list of key phrases
     * 
     * @param kps
     *            Key phrases to search by
     * @return A list of related synonyms
     */
    List<Synonym> findByKp(List<KeyPhrase> kps);

}
