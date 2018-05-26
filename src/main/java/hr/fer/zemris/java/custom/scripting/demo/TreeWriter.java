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

public class TreeWriter {

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
		WriterVisitor visitor=new WriterVisitor();
		parser.getDocumentNode().accept(visitor);
	}

	public static class WriterVisitor implements INodeVisitor {

		@Override
		public void visitTextNode(TextNode node) {
			System.out.println(node.getText());
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			System.out.println(node.toString());
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			System.out.println(node.toString());
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			System.out.println(node.toString());
		}

	}
}
