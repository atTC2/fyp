package xyz.tomclarke.fyp.gui.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PaperRepository extends CrudRepository<Paper, Long> {

    /**
     * Finds papers with a given status (0 - not processed, 1 - kp extraction
     * completed, 2 - kp classification, 3 - relation extraction completed)
     * 
     * @param status
     *            The processing status
     * @return A list of papers with the given status
     */
    List<Paper> findByStatus(Integer status);

}
