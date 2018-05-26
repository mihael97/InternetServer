package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Razred koji implementira cijeli dokument. Nasljeduje klasu Node
 * 
 * @author Mihael
 * 
 */
public class DocumentNode extends Node {

	/**
	 * Method provides document node visiting to given visitor
	 * 
	 * @param visitor
	 *            - object used for visiting
	 */
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitDocumentNode(this);
	}

}
