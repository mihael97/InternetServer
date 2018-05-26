package hr.fer.zemris.java.custom.scripting.nodes;

import java.util.ArrayList;

/**
 * Razred koji implementira clana strukture
 * 
 * @author ime
 *
 */
public abstract class Node {
	/**
	 * Referenca na kolekciju za pohranu djece
	 */
	private ArrayList<Object> collection = null;

	/**
	 * Metoda koja dodaje dijete u kolekciju(polje)
	 * 
	 * @param child
	 *            - dijete koje zelimo dodati,ne smije biti null!
	 */
	public void addChildNode(Node child) {
		if (collection == null) {
			collection = new ArrayList<>();
		}

		collection.add(child);
	}

	/**
	 * Metoda koja vraca broj djece koji se nalazi u stablu
	 * 
	 * @return int - broj djece
	 */
	public int numberOfChildren() {
		if (collection == null)
			return 0;
		return collection.size();
	}

	/**
	 * Metoda koja vraca clana na odredenom indexu,odnosno iznimku
	 * 
	 * @param index
	 * @return Node
	 * @throws IndexOutOfBoundsException
	 *             - ako index nije u rasponu
	 */
	public Node getChild(int index) {
		return (Node) collection.get(index);
	}

	/**
	 * Abstract method provides visiting for given visitor
	 * 
	 * @param visitor
	 *            - object which is use for visiting
	 */
	public abstract void accept(INodeVisitor visitor);
}
