package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PaperRepository extends CrudRepository<PaperDAO, Long> {

    /**
     * Finds papers with a given status (0 - not processed, 1 - kp extraction, 2 -
     * kp, 3 - relation extraction, 4 - finished)
     * 
     * @param status
     *            The processing status
     * @return A list of papers with the given status
     */
    List<PaperDAO> findByStatus(Long status);

    /**
     * Finds papers with text matching the given regex (regex is case insensitive)
     * 
     * @param regex
     *            The regex to match on
     * @return The list of papers matching the given regex
     */
    @Query(value = "SELECT * FROM paper WHERE text regexp :regex", nativeQuery = true)
    List<PaperDAO> findByTextWithRegex(@Param("regex") String regex);

}
