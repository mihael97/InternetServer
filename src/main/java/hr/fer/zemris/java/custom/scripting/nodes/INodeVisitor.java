package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Interface provides methods for visiting every type of node. Supports
 * <ul>
 * <li>text node</li>
 * <li>for loop node</li>
 * <li>echo node</li>
 * <li>document node</li>
 * </ul>
 * 
 * node visiting
 * 
 * @author Mihael
 *
 */
public interface INodeVisitor {
	/**
	 * Called when visitor want to visit {@link TextNode}
	 * 
	 * @param node
	 *            - node
	 */
	public void visitTextNode(TextNode node);

	/**
	 * Called when visitor want to visit {@link ForLoopNode}
	 * 
	 * @param node
	 *            - node
	 */
	public void visitForLoopNode(ForLoopNode node);

	/**
	 * Called when visitor want to visit {@link EchoNode}
	 * 
	 * @param node
	 *            - node
	 */
	public void visitEchoNode(EchoNode node);

	/**
	 * Called when visitor want to visit {@link DocumentNode}
	 * 
	 * @param node
	 *            - node
	 */
	public void visitDocumentNode(DocumentNode node);
}
