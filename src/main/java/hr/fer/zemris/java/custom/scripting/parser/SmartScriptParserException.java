package hr.fer.zemris.java.custom.scripting.parser;

/**
 * Javni razred koji nasljeduje RuntimeException. Iznimka se koristi pri
 * javljaju pogreska u radu s parserom poput krivo zadanih imena
 * 
 * @author Mihael
 *
 */
public class SmartScriptParserException extends RuntimeException {

	/**
	 * SerialVersionUID zadan od strane prevoditelja
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Javni konstruktor koji prima String s opisom iznimke i prosljeduje ga
	 * nadredeno konstruktoru u razredu RuntimeException
	 * 
	 * @param exp
	 *            - niz kojeg zelimo ispisati,pojasnjenje
	 * @param e
	 */
	public SmartScriptParserException(String exp) {
		super(exp);
	}

	/**
	 * Javni konstruktor koji kao argumente prima String kao opisnik iznimke i
	 * referencu na iznimku
	 * 
	 * @param message
	 *            - opisnik iznimke koju smo dobili kao argument
	 * @param cause
	 *            - iznimka koja je uzrokavala {@link SmartScriptParserException}
	 */
	public SmartScriptParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
