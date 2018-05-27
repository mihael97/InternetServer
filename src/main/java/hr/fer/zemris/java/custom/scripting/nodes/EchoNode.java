package hr.fer.zemris.java.custom.scripting.nodes;

import java.util.Arrays;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;

/**
 * Razred koji implementira naredbu koja stvara neki izlaz. Nasljeduje klasu
 * Node. Sadrzi polje Elementa
 * 
 * @author Mihael
 *
 */
public class EchoNode extends Node {
	/**
	 * Elementi
	 */
	Element[] elements;

	/**
	 * Javni konstruktor koji prima referencu na elemente i inicijalizira ih
	 * 
	 * @param elements
	 *            - elementi
	 */
	public EchoNode(Element[] elements) {
		super();
		this.elements = Arrays.copyOf(elements, elements.length);

		System.out.println("\nEcho:");
		for (Element ele : elements) {
			if (ele != null) {
				System.out.print(ele.asText());

				if (ele instanceof ElementFunction) {
					System.out.print(" FUNKCIJA");
				}

				System.out.print(" - ");
			}
		}

		System.out.println("\n");
	}

	/**
	 * Metoda koja vraca sve elemente u obliku polje Elemenata
	 * 
	 * @return Element[]
	 */
	public Element[] getElements() {
		return elements;
	}

	/**
	 *
	 * Metoda koja vraca ispis EchoNodea sa svim elementima u primjerenom obliku
	 * 
	 * @return string ispis svih elemenata
	 *
	 */
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder().append("{$= ");

		for (Element ele : elements) {
			if (ele != null) {
				if (ele instanceof ElementString) {
					string.append(((ElementString) ele).forParse() + " ");
				} else {
					string.append(ele.asText() + " ");
				}
			}
		}

		string.append("$}");

		return string.toString();
	}

	/**
	 * Method accepts visitor when he want to visit {@link EchoNode}
	 * 
	 * @param visitor
	 *            - visitor
	 */
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}
}
