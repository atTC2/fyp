package xyz.tomclarke.fyp.nlp.paper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the save/load of Paper
 * 
 * The unused suppression is for the Paper objects created but never used, as
 * they should auto-save
 * 
 * @author tbc452
 *
 */
@SuppressWarnings("unused")
public class TestSerialisation {

    private static final String testFileName = "TEST.txt";
    private static final String sampleText = "TESTTESTTESTTEST";

    @Before
    public void Initialise() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFileName, true));
        writer.write(sampleText);
        writer.close();
    }

    @After
    public void CleanUp() {
        // Clean up any file created
        new File(testFileName).delete();
        new File(testFileName + Paper.SER_FILE_EXT).delete();
    }

    @Test
    public void TestMakeSerFile() {
        Paper paper = new TextPaper(testFileName);

        // A file should have been made now...
        Assert.assertTrue(new File(testFileName + Paper.SER_FILE_EXT).exists());
    }

    @Test
    public void TestSerFileReadIn() {
        // First create a file, then read it back in
        Paper paper = new TextPaper(testFileName);

        // A file should have been made now...
        Assert.assertTrue(new File(testFileName + Paper.SER_FILE_EXT).exists());

        // Now create a new object and it should have the same text...
        Paper paperLoaded = new TextPaper(testFileName);
        // Should load it in straight away
        Assert.assertEquals(paperLoaded.getText(), sampleText);
    }

    @Test
    public void TestSerFileMultipleWrites() {
        // First create a file, then read it back in
        Paper paper = new TextPaper(testFileName);

        // A file should have been made now...
        Assert.assertTrue(new File(testFileName + Paper.SER_FILE_EXT).exists());

        // Now create a new object and it should have the same text...
        Paper paperLoaded = new TextPaper(testFileName);
        // Should load it in straight away
        Assert.assertEquals(paperLoaded.getText(), sampleText);

        // Now change the text, save it, try loading it again
        paper.setText(sampleText + sampleText);
        paperLoaded = new TextPaper(testFileName);
        Assert.assertEquals(paperLoaded.getText(), sampleText + sampleText);
    }

    @Test
    public void TestReadInNotOverwrite() throws IOException {
        // First create a file, then read it back in
        Paper paper = new TextPaper(testFileName);

        // A file should have been made now...
        Assert.assertTrue(new File(testFileName + Paper.SER_FILE_EXT).exists());

        // If it reads in a file, it shouldn't change it...
        new File(testFileName).delete();
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFileName, true));
        writer.write("NOT WHAT THE GET TEXT METHOD SHOULD RETURN");
        writer.close();

        // Now create a new object and it should have the same text...
        Paper paperLoaded = new TextPaper(testFileName);
        // Should load it in and NOT change the text
        Assert.assertEquals(paperLoaded.getText(), sampleText);
    }

}
