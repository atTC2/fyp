package xyz.tomclarke.fyp.nlp.svm;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.util.NlpError;

/**
 * Denotes some requirements of what an SVM needs to do
 * 
 * @author tbc452
 *
 */
public abstract class BaseSvm implements Serializable {

    private static final long serialVersionUID = 8718751442830249634L;

    private static final Logger log = LogManager.getLogger(BaseSvm.class);

    protected svm_parameter param;
    protected svm_model model;
    protected svm_problem problem;

    public BaseSvm() {
        svm.svm_set_print_string_function(new SvmLogger());

        // Construct the (default) parameter object
        param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.gamma = 1 / 2.0; // 1 / number of features
        param.cache_size = 1024;
        param.eps = 0.001;
        param.C = 100.0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        param.shrinking = 0;
        param.probability = 0;
    }

    /**
     * Train the SVM on loaded data
     * 
     * @throws NlpError
     *             If the training data isn't suitable for use
     */
    public void train() throws NlpError {
        log.info("Training SVM");
        String paramCheck = svm.svm_check_parameter(problem, param);
        if (paramCheck == null) {
            // Fine to train on
            model = svm.svm_train(problem, param);
            log.info("Finished training");
        } else {
            // Something is wrong...
            throw new NlpError(paramCheck);
        }
    }

    /**
     * Predict if a word (translated to a set of nodes) is a keyword.
     * 
     * @param nodes
     *            The processed token information
     * @return Whether the token is a key phrase
     */
    public boolean predict(svm_node[] nodes) {
        double prediction = svm.svm_predict(model, nodes);
        return prediction > 0.0;
    }

    /**
     * Creates a new SVM node
     * 
     * @param index
     * @param value
     * @return
     */
    protected svm_node makeNewNode(int index, double value) {
        svm_node node = new svm_node();
        node.index = index;
        node.value = value;
        if (!Double.isFinite(value)) {
            node.value = 0.0;
        }
        return node;
    }

    /**
     * Perform cross-validation with set C and gamma values
     * 
     * @param c
     *            The C value to test
     * @param gamma
     *            The gamma value to test
     * @return The percentage accuracy
     */
    public double doCrossValidation(double c, double gamma) {
        log.info("Problem l :" + problem.l);
        param.C = c;
        param.gamma = gamma;
        log.info("C: " + param.C + " gamma: " + param.gamma);
        double[] target = new double[problem.l];
        svm.svm_cross_validation(problem, param, 5, target);
        int total_correct = 0;
        for (int i = 0; i < problem.l; i++) {
            if (target[i] == problem.y[i]) {
                ++total_correct;
            }
        }
        double accuracy = 100.0 * total_correct / problem.l;
        log.info("Cross Validation Accuracy = " + accuracy + "%");
        return 100.0 * total_correct / problem.l;
    }
}
