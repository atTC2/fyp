package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

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

}
