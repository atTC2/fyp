package xyz.tomclarke.fyp.gui.dao;

import org.springframework.data.repository.CrudRepository;

public interface NlpObjectRepository extends CrudRepository<NlpObjectDAO, Long> {

    /**
     * Finds NLP objects by their label
     * 
     * @param label
     *            The label to search for
     * @return The NLP object with the given label
     */
    NlpObjectDAO findByLabel(String label);

    /**
     * Counts the number of entries with the given label (there can only ever by 1
     * or 0 as it is unique).
     * 
     * @param label
     *            The label to search for
     * @return How many of a given label exist
     */
    long countByLabel(String label);

}
