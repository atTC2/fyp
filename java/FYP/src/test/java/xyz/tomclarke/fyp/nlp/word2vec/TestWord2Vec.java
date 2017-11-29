package xyz.tomclarke.fyp.nlp.word2vec;

import java.util.Scanner;

import org.deeplearning4j.models.word2vec.Word2Vec;

/**
 * A test class to let you mess around with Google News and Word2Vec (very much
 * just testing fun)
 * 
 * @author tbc452
 *
 */
public class TestWord2Vec {

    public static void main(String[] args) throws Exception {
        System.out.println("Loading Word2Vec...");
        Word2Vec vec = Word2VecProcessor.loadGoogleNewsVectors();
        System.out.println("Loaded");
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        while (!exit) {
            String input = scanner.nextLine();
            switch (input) {
            case "exit":
                exit = true;
                break;
            default:
                String clazzInput = scanner.nextLine();
                System.out.println("Processing: " + input + " + " + clazzInput);
                System.out.println(vec.similarity(input, clazzInput));
            }
        }
        scanner.close();
    }

}
