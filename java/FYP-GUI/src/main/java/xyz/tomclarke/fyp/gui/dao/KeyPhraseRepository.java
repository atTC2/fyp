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

}
