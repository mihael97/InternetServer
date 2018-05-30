package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Razred koji implementira tekstualni podatak i nasljeduje klasu Node
 * 
 * @author Mihael
 *
 */
public class TextNode extends Node {
	/**
	 * Privatna varijabla koja sprema tekst
	 */
	private String text;

	/**
	 * Javni konstruktor koji inicijalizira tekst
	 * 
	 * @param text
	 */
	public TextNode(String text) {
		super();
		this.text = text;
	}

	/**
	 * Metoda koja vraca vrijednost teksta
	 * 
	 * @return String
	 */
	public String getText() {
		return text;
	}

	/**
	 * Metoda koja pretvara vrijednost elementa u oblik koji je pogodan za
	 * parsiranje
	 * 
	 * @return vrijednost cvora u obliku pogodnom za parser
	 */
	public String prepareForOutput() {
		StringBuilder builder = new StringBuilder();
		char[] array = text.toCharArray();

		for (int i = 0; i < array.length; i++) {
			if (array[i] == '\\') {
				builder.append(array[i]);
			} else if (array[i] == '{') {
				builder.append("\\");
			}

			builder.append(array[i]);
		}

		return builder.toString();
	}

	/**
	 * Method is called when visitor want to pass trough {@link TextNode}
	 * 
	 * @param visitor
	 *            - visitor
	 */
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}

}
