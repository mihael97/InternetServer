package hr.fer.zemris.java.custom.collections;

/**
 * Razred koji implementira iznimku kada je stog prazan
 * @author Mihael
 *
 */
@SuppressWarnings("serial")
public class EmptyStackException extends RuntimeException {
	
	/**
	 * Javni konstruktor koji salje poruku za ispis nadredenom konstruktoru
	 */
	public EmptyStackException() {
		super("Stog je prazan!");
	}
}
