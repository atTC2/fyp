package xyz.tomclarke.fyp.nlp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Loads training and test papers for use in testing algorithms
 * 
 * @author tbc452
 *
 */
public class RandomTests {

    /**
     * Checks the kp sanitisation regexs
     */
    @Test
    public void testSampleRegex() {
        String[] prees = new String[] { "[", "\"", "{", "(", "A", "a", "1" };
        String[] afters = new String[] { "]", "\"", "}", ")", "A", "a", "1" };

        for (String pre : prees) {
            Assert.assertFalse("Pre failed: " + pre, pre.matches("[^a-zA-Z0-9\\[\"{(]"));
        }
        for (String after : afters) {
            Assert.assertFalse("After failed: " + after, after.matches("[^a-zA-Z0-9)/]}\"]"));
        }
    }

}
