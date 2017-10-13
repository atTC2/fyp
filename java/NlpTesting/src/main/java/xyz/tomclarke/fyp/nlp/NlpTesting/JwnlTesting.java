package xyz.tomclarke.fyp.nlp.NlpTesting;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;

public class JwnlTesting {

	public static void main(String[] args) throws JWNLException {
		new JwnlTesting();
	}

	public JwnlTesting() throws JWNLException {
		JWNL.initialize(getClass().getClassLoader().getResourceAsStream("jwnl.xml"));
	}
}
