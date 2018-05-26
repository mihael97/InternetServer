package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Razred koji implementira operator
 * 
 * @author Mihael
 *
 */
public class ElementOperator extends Element {
	/**
	 * Varijabla gdje pohranjujemo operator u obliku Stringa
	 */
	private String symbol;

	/**
	 * Konstruktor koji inicijalizira operator s kojim se obavljaju racunale
	 * operacije. Podrzani operatori su +,-,/,%,* i ^
	 * 
	 * @param value
	 * @throws NullPointerException
	 *             - ako je vrijednost null
	 */
	public ElementOperator(String value) {
		// TODO Auto-generated constructor stub
		if (value == null) {
			throw new NullPointerException("Vrijednost je null!");
		}
		this.symbol = value;
	}

	/**
	 * Metoda vraca operator u obliku Stringa
	 * 
	 * @return String
	 */
	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return symbol;
	}
}
