package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Razred koji implementira neki konstantan zapis znakova u obliku Stringa
 * 
 * @author Mihael
 *
 */
public class ElementString extends Element {
	/**
	 * Vrijednost u obliku Stringa
	 */
	private String value;

	/**
	 * Konstruktor koji inicijalizira vrijednost Stringa
	 * 
	 * @param value
	 *            - vrijednost
	 * @throws NullPointerException
	 *             - ako je vrijednost null
	 */
	public ElementString(String value) {
		// TODO Auto-generated constructor stub
		if (value == null) {
			throw new NullPointerException("Vrijednost je null!");
		}
		this.value = value;
				
	}

	/**
	 * Metoda vraca String u jednakom obliku
	 * 
	 * @return String
	 */
	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return value;
	}

	/**
	 * Metoda koja pretvara vrijednost elementa u zapis procitan sa ulaza. Takav
	 * zapis je pogodan za parsiranje
	 * 
	 * @return zapis vrijednosti elemnta u obliku pogodnom za parser
	 */
	public String forParse() {
		char[] array = value.toCharArray();
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			if (array[i] == '\\' || array[i] == '\"' && i != 0 && i != (array.length - 1)) {
				builder.append("\\");
			}

			builder.append(array[i]);
		}

		return builder.toString();
	}

}
