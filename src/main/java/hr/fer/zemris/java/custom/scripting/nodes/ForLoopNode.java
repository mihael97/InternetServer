package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * Razred koji predstavlja for petlju i nasljeduje klasu Node. Varijable su
 * naziv varijable,pocetni izraz,zavrsni izraz i izraz koji predstavlja korak.
 * Prve tri vrijednosti ne mogu biti null,dok cetvrta moze
 * 
 * @author Mihael
 *
 */
public class ForLoopNode extends Node {
	/**
	 * Vrijednost
	 */
	private ElementVariable variable;
	/**
	 * Pocetni izraz
	 */
	private Element startExpression;
	/**
	 * Zavrsni izraz
	 */
	private Element endExpression;
	/**
	 * 'Preskakuci izraz'
	 */
	private Element stepExpression;

	/**
	 * Javni konstruktor koji inicijalizira sve varijable
	 * 
	 * @param variable
	 *            - naziv varijable
	 * @param startExpression
	 *            - pocetni izraz
	 * @param endExpression
	 *            - zavrsni izraz
	 * @param stepExpression
	 *            - izraz koji predstavlja korak
	 * 
	 * @throws NullPointerException
	 *             - ako je jedan od prva tri izraza null
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression, Element endExpression,
			Element stepExpression) {
		super();
		if (variable == null || startExpression == null || endExpression == null) {
			throw new IllegalArgumentException("Jedan od predanih argumenata je null!");
		}

		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;

		//System.out.println("FOR LOOP  - " + toString());
	}

	/**
	 * Metoda koja vraca naziv varijable
	 * 
	 * @return {@link ElementVariable}
	 */
	public ElementVariable getVariable() {
		return variable;
	}

	/**
	 * Metoda koja vraca pocetni izraz
	 * 
	 * @return {@link Element}
	 */
	public Element getStartExpression() {
		return startExpression;
	}

	/**
	 * Metoda koja vraca zavrsni izraz
	 * 
	 * @return {@link Element}
	 */
	public Element getEndExpression() {
		return endExpression;
	}

	/**
	 * Metoda koja vraca izraz koji predstavlja korak
	 * 
	 * @return {@link Element}
	 */
	public Element getStepExpression() {
		return stepExpression;
	}

	/**
	 * Metoda koja vraca ispis FORN u prihvatljivom obliku
	 * 
	 * @return String kao ispis FOR petlje
	 */
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder().append("{$ FOR ").append(variable.asText()).append(" ");

		if (startExpression instanceof ElementString) {
			string.append(((ElementString) startExpression).forParse());
		} else {
			string.append(startExpression.asText());
		}

		string.append(" ");

		if (endExpression instanceof ElementString) {
			string.append(((ElementString) endExpression).forParse());
		} else {
			string.append(endExpression.asText());
		}

		string.append(" ");

		if (stepExpression != null) {
			if (stepExpression instanceof ElementString) {
				string.append(((ElementString) stepExpression).forParse());
			} else {
				string.append(stepExpression.asText());
			}

			string.append(" ");
		}

		string.append("$}");

		return string.toString();
	}

	/**
	 * Method accepts visitor when he want to visit {@link ForLoopNode}
	 * 
	 * @param visitor
	 *            - visitor
	 */
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
	}

}
