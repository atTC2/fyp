package xyz.tomclarke.fyp.nlp.svm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import libsvm.svm_print_interface;

/**
 * Used for logging libsvm messages
 * 
 * @author tbc452
 *
 */
public class SvmLogger implements svm_print_interface {

    private static final Logger log = LogManager.getLogger(SvmLogger.class);

    @Override
    public void print(String message) {
        message = message.trim();
        String formatedMessage = "(libsvm) " + message;
        if (message.isEmpty()) {
            // An empty message... hmm
            return;
        } else if (message.length() < 2) {
            // Message is a character long, so we probably don't care about it
            log.debug(message);
        } else if (message.contains("WARNING")) {
            // It's a warning
            log.warn(formatedMessage);
        } else {
            // No other special cases seen yet
            // log everything else
            log.info(formatedMessage);
        }
    }

}
