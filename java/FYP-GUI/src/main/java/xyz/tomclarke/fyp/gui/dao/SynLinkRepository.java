package xyz.tomclarke.fyp.gui.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SynLinkRepository extends CrudRepository<SynLinkDAO, Long> {

    /**
     * Counts the synonyms for a given paper
     * 
     * @param paper
     *            The paper
     * @return The number of synonyms for the paper
     */
    @Query(value = "SELECT COUNT(DISTINCT sl.id) FROM syn_link sl, synonym s, key_phrase kp, paper p WHERE p.id = :paper AND p.id = kp.paper AND kp.id = s.kp", nativeQuery = true)
    Long countByPaper(@Param("paper") PaperDAO paper);

}
