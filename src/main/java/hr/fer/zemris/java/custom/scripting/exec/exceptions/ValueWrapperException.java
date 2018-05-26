package hr.fer.zemris.java.custom.scripting.exec.exceptions;


/**
 * Razred predstavlja iznimku koja se može koristit u {@link ValueWrapper}
 * 
 * @author Mihael
 *
 */
public class ValueWrapperException extends RuntimeException {

	/**
	 * serialVerionID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Javni konstruktor
	 * 
	 * @param str
	 *            - opis iznimke
	 */
	public ValueWrapperException(String str) {
		super(str);
	}
}
