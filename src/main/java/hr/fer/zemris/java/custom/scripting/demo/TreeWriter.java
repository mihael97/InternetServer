package hr.fer.zemris.java.custom.scripting.demo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * Class implements visitor who is going trough whole file and make file tree
 * 
 * @author Mihael
 *
 */
public class TreeWriter {

	/**
	 * Main program
	 * 
	 * @param args
	 *            - in use,length must be 1 where argument is path to file
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Program must recive one argument!");
			return;
		}

		String body = null;

		try {
			body = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return;
		}

		SmartScriptParser parser = new SmartScriptParser(body);
		WriterVisitor visitor = new WriterVisitor();
		parser.getDocumentNode().accept(visitor);
	}

	/**
	 * Class represents {@link INodeVisitor} implementation with all methods
	 * 
	 * @author Mihael
	 *
	 */
	public static class WriterVisitor implements INodeVisitor {

		/**
		 * Called when we are visiting {@link TextNode}
		 * 
		 * @param node
		 *            - current {@link TextNode} we want to visit
		 */
		@Override
		public void visitTextNode(TextNode node) {
			System.out.println(node.getText());
		}

		/**
		 * Called when we are visiting {@link ForLoopNode}
		 * 
		 * @param node
		 *            - current {@link ForLoopNode} we want to visit
		 */
		@Override
		public void visitForLoopNode(ForLoopNode node) {
			System.out.println(node.toString());
		}

		/**
		 * Called when we are visiting {@link TextNode}
		 * 
		 * @param node
		 *            - current {@link TextNode} we want to visit
		 */
		@Override
		public void visitEchoNode(EchoNode node) {
			System.out.println(node.toString());
		}

		/**
		 * Called when we are visiting {@link DocumentNode}
		 * 
		 * @param node
		 *            - current {@link DocumentNode} we want to visit
		 */
		@Override
		public void visitDocumentNode(DocumentNode node) {
			System.out.println(node.toString());
		}

	}
}
