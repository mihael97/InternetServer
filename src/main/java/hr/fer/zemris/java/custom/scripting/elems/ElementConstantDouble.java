package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Razred koji implementira double vrijednost. Nasljeduje razred Element od kuda
 * overridea metodu za ispis asText. Konstruktor prima samo primitivnu double
 * vrijednost
 * 
 * @author Mihael
 *
 */
public class ElementConstantDouble extends Element {
	/**
	 * Vrijednost u obliku doublea
	 */
	private double value;

	/**
	 * Javni konstruktor koji inicijalizira vrijednost doublea
	 * 
	 * @param value
	 *            - double vrijednost
	 */
	public ElementConstantDouble(double value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}

	/**
	 * Metoda vraca primitivnu vrijednost doublea u obliku Stinga
	 * 
	 * @return String
	 */
	@Override

	public String asText() {
		// TODO Auto-generated method stub
		return Double.valueOf(value).toString();
	}
}
