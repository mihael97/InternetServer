package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Razred koji predstavlja iznimku koja se baca u lexeru ako dode do
 * nedopustenih znakova ili ostalih
 * 
 * @author Mihael
 *
 */
public class LexerException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Glavni konstruktor koji prima kao argument String(opis iznimke) i prosljeduje
	 * nadredenom konstruktoru iz klase RunTimeException
	 * 
	 * @param string
	 */
	public LexerException(String string) {
		super(string);
	}
}
