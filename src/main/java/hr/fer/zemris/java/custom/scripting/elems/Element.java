package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Javni razred kojim implementiramo svaki element parsiranja. Razred sadrzi
 * samo metodu asText koja vraca vrijednost u obliku Stringa. Svi oblici mogucih
 * elemenata nasljedivati ce ovu klasu
 * 
 * @author Mihael
 *
 */
public class Element {
	/**
	 * Metoda koja vraca objekt u obliku Stringa
	 * 
	 * @return objekt kao String
	 */
	public String asText() {
		return "";
	}
}
