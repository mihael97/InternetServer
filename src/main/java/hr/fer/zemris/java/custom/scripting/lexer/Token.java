package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Razred koji predstavlja strukturu koju proizvodi lexer i salje ju parseru na
 * danju obradu
 * 
 * @author Mihael
 *
 */
public class Token {
	/**
	 * Referenca na {@link TypeToken} - vrstu podataka koju token sadrzi
	 */
	private final TypeToken type;
	/**
	 * Vrijednost pohranjena u tokemu
	 */
	private final Object value;

	/**
	 * Konstruktor koji stavara novi token
	 * 
	 * @param type
	 *            - vrsta tokena
	 * @param value
	 *            - vrijednost tokena
	 */
	public Token(TypeToken type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	/**
	 * Metoda koja vraca vrstu tokena
	 * 
	 * @return {@link TypeToken} - vrsta tokena
	 */
	public TypeToken getType() {
		return type;
	}

	/**
	 * Metoda koja vraca vrijednost cvora. Moguci oblici Objekt su Double,Integer i
	 * String
	 * 
	 * @return vrijednost cvora
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Metoda koja vraca prikaz Tokena u Stringu. Prikaz tokena implementira se kao
	 * vrijednost tokena u obliku Stringa
	 * 
	 * @return String - vrijednost tolena
	 */
	@Override
	public String toString() {
		if (value instanceof Integer || value instanceof Double)
			return value.toString();
		else {
			return (String) value;
		}
	}

}
