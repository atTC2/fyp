package xyz.tomclarke.fyp.gui.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.model.KPViewCloudEntity;
import xyz.tomclarke.fyp.gui.model.KPViewRow;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Builds and retains a cache for key phrase cloud drawing
 * 
 * @author tbc452
 *
 */
@Service
public class KeyPhraseCloudCache {

    private static final Logger log = LogManager.getLogger(KeyPhraseCloudCache.class);

    @Autowired
    private KeyPhraseRepository kpRepo;
    private List<Paper> trainingPapers;
    private List<KPViewCloudEntity> taskCloud;
    private List<KPViewCloudEntity> processCloud;
    private List<KPViewCloudEntity> materialCloud;
    private List<KPViewRow> taskRows;
    private List<KPViewRow> processRows;
    private List<KPViewRow> materialRows;

    /**
     * Updates the cache every 30 minutes
     * 
     * @throws Exception
     */
    @PostConstruct
    public void updateCache() {
        log.info("Updating KP cloud cache");
        // Ensure we have the training papers
        if (trainingPapers == null) {
            // Need to ensure they're annotated
            trainingPapers = NlpUtil.loadAndAnnotatePapers(LoadPapers.class, false);
        }

        // Get key phrase information from DB
        List<KeyPhraseDAO> taskKps = kpRepo.findByClassification(Classification.TASK.toString());
        List<KeyPhraseDAO> processKps = kpRepo.findByClassification(Classification.PROCESS.toString());
        List<KeyPhraseDAO> materialKps = kpRepo.findByClassification(Classification.MATERIAL.toString());

        // Update each of the cloud data supplies
        taskCloud = generateCloudData(taskKps);
        processCloud = generateCloudData(processKps);
        materialCloud = generateCloudData(materialKps);

        // Update each of the key phrase rows
        taskRows = generateKPRows(taskKps);
        processRows = generateKPRows(processKps);
        materialRows = generateKPRows(materialKps);

        log.info("Updated KP cloud cache");
    }

    /**
     * Gets the task cloud data
     * 
     * @return
     */
    public List<KPViewCloudEntity> getTaskCloud() {
        if (taskCloud == null) {
            throw new NullPointerException();
        }
        return taskCloud;
    }

    /**
     * Gets the process cloud data
     * 
     * @return
     */
    public List<KPViewCloudEntity> getProcessCloud() {
        if (processCloud == null) {
            throw new NullPointerException();
        }
        return processCloud;
    }

    /**
     * Gets the material cloud data
     * 
     * @return
     */
    public List<KPViewCloudEntity> getMaterialCloud() {
        if (materialCloud == null) {
            throw new NullPointerException();
        }
        return materialCloud;
    }

    /**
     * Gets the task row data
     * 
     * @return
     */
    public List<KPViewRow> getTaskRows() {
        if (taskRows == null) {
            throw new NullPointerException();
        }
        return taskRows;
    }

    /**
     * Gets the process row data
     * 
     * @return
     */
    public List<KPViewRow> getProcessRows() {
        if (processRows == null) {
            throw new NullPointerException();
        }
        return processRows;
    }

    /**
     * Gets the material row data
     * 
     * @return
     */
    public List<KPViewRow> getMaterialRows() {
        if (materialRows == null) {
            throw new NullPointerException();
        }
        return materialRows;
    }

    /**
     * Generate text cloud data for a set of key phrases
     * 
     * @param kps
     *            The key phrases to generate the cloud data from
     * @return The new set of cloud entities
     */
    private List<KPViewCloudEntity> generateCloudData(List<KeyPhraseDAO> kps) {
        // Then, add all words to a map of words, counting instances and no punctuation
        // allowed!
        Map<String, Double> mapOfTokens = new HashMap<String, Double>();
        for (KeyPhraseDAO kp : kps) {
            String[] tokens = kp.getText().toLowerCase().replaceAll("[^a-z ]", " ").split(" ");
            for (String token : tokens) {
                // Don't show noise
                if (!token.isEmpty() && token.length() > 1) {
                    if (!mapOfTokens.containsKey(token)) {
                        // Found it once, list it's TF-IDF
                        try {
                            mapOfTokens.put(token, NlpUtil.calculateTfIdf(token, getPaperOfKp(kp), trainingPapers));
                        } catch (ClassNotFoundException | IOException e) {
                            log.error("Problem adding token to cloud", e);
                        }
                    }
                }
            }
        }

        // Finally, convert them to the cloud entity form
        List<KPViewCloudEntity> entities = new ArrayList<KPViewCloudEntity>();
        for (Map.Entry<String, Double> tokenAndCount : mapOfTokens.entrySet()) {
            entities.add(new KPViewCloudEntity(tokenAndCount.getKey(), tokenAndCount.getValue()));
        }

        return entities;
    }

    /**
     * Gets the paper for a key phrase
     * 
     * @param kp
     *            The key phrase
     * @return The key phrase's paper
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private Paper getPaperOfKp(KeyPhraseDAO kp) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(kp.getPaper().getParse());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Paper) ois.readObject();
    }

    /**
     * Generates a key phrase row
     * 
     * @param kps
     *            The key phrases to generate rows of
     * @return A list of rows representing the key phrases
     */
    private List<KPViewRow> generateKPRows(List<KeyPhraseDAO> kps) {
        List<KPViewRow> kpRows = new ArrayList<KPViewRow>();
        for (KeyPhraseDAO kp : kps) {
            kpRows.add(new KPViewRow(kp.getText(), kp.getPaper().getTitle(), kp.getPaper().getId()));
        }
        return kpRows;
    }

}
