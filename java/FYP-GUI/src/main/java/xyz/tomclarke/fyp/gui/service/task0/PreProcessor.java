package xyz.tomclarke.fyp.gui.service.task0;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;

/**
 * Fills in the paper database object and adds annotation information (an actual
 * Paper object)
 * 
 * @author tbc452
 *
 */
@Component
public class PreProcessor implements NlpProcessor {

    private static final Logger log = LogManager.getLogger(PreProcessor.class);
    @Autowired
    private PaperRepository paperRepo;
    private Annotator ann;

    @Override
    public void loadObjects() throws Exception {
        ann = new Annotator();
    }

    @Override
    public boolean processPaper(PaperDAO paper) throws Exception {
        List<Paper> papers = LoadPapers.loadNewPapers(paper.getLocation(), false);

        // Check each case
        if (papers.isEmpty()) {
            // Paper could not be loaded
            log.warn("Paper NOT FOUND, ID " + paper.getId());
            return false;
        } else if (papers.size() == 1) {
            // Just this paper was loaded
            setupPaper(paper, papers.get(0));
            log.info("Paper setup, ID " + paper.getId());
            return true;
        } else {
            // Many papers, convert this paper to the first one and then make lots of new
            // ones)
            setupPaper(paper, papers.get(0));
            // And now the new papers
            for (int i = 1; i < papers.size(); i++) {
                try {
                    PaperDAO newPaper = new PaperDAO();
                    setupPaper(newPaper, papers.get(i));
                    newPaper.setStatus(Long.valueOf(1));
                    paperRepo.save(newPaper);
                } catch (Exception e) {
                    // If one fails, we want to give the others a chance
                    log.error("Error pre-processing child paper: " + papers.get(i).getLocation(), e);
                }
            }
            log.info("Paper setup, ID " + paper.getId() + " with " + (papers.size() - 1)
                    + " extra paper entries created");
            return true;
        }
    }

    /**
     * Sets up he database version of a paper
     * 
     * @param paper
     *            The database paper
     * @param loadedPaper
     *            The paper from the NLP project
     * @throws IOException
     *             What went wrong
     */
    private void setupPaper(PaperDAO paper, Paper loadedPaper) throws IOException {
        // Annotate the paper
        loadedPaper.setAnnotations(ann.annotate(loadedPaper.getText()));

        // Import the information into the database entry
        paper.setLocation(loadedPaper.getLocation());
        paper.setTitle(loadedPaper.getTitle());
        paper.setAuthor(loadedPaper.getAuthor());
        paper.setText(loadedPaper.getText());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(loadedPaper);
        paper.setParse(baos.toByteArray());
    }

    @Override
    public void unload() throws Exception {
        ann = null;
        System.gc();
    }

}
