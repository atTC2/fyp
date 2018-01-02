package xyz.tomclarke.fyp.gui.dao;

import org.springframework.data.repository.CrudRepository;

public interface NlpObjectRepository extends CrudRepository<NlpObject, Long> {

    /**
     * Finds NLP objects by their label
     * 
     * @param label
     *            The label to search for
     * @return The NLP object with the given label
     */
    NlpObject findByLabel(String label);

}
