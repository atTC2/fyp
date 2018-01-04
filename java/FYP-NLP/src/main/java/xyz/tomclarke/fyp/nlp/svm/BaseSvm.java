package xyz.tomclarke.fyp.nlp.svm;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

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
        // 1 / number of features
        param.gamma = 1 / 2.0;
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
     * @throws Exception
     *             If the training data isn't suitable for use
     */
    public void train() throws Exception {
        log.info("Training SVM");
        String paramCheck = svm.svm_check_parameter(problem, param);
        if (paramCheck == null) {
            // Fine to train on
            model = svm.svm_train(problem, param);
            log.info("Finished training");
        } else {
            // Something is wrong...
            throw new Exception(paramCheck);
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
        log.debug("Prediction: " + prediction);
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
     * Performs cross validation - warning, VERY slow
     */
    public void doCrossValidation() {
        // Do cross validation to find the best parameters
        log.info("C: " + param.C + " gamma: " + param.gamma);
        double[] target = new double[problem.l];
        svm.svm_cross_validation(problem, param, 5, target);
        log.info("C: " + param.C + " gamma: " + param.gamma);
        int total_correct = 0;
        for (int i = 0; i < problem.l; i++) {
            if (target[i] == problem.y[i]) {
                ++total_correct;
            }
        }
        // Currently 69.67793310918421% for KP extraction
        log.info("Cross Validation Accuracy = " + 100.0 * total_correct / problem.l + "%");
    }
}
