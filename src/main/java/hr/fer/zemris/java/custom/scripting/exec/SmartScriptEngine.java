package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

public class SmartScriptEngine {
	/**
	 * Document node
	 */
	private DocumentNode documentNode;
	/**
	 * Request
	 */
	private RequestContext requestContext;
	private ObjectMultistack multistack = new ObjectMultistack();
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.getText().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			ValueWrapper start = new ValueWrapper(node.getStartExpression().asText());
			Object end = node.getEndExpression().asText();
			Object step = node.getStepExpression().asText();
			String name = node.getVariable().asText();
			multistack.push(name, start);

			while (start.numCompare(end) < 0) {
				for (int i = 0, n = node.numberOfChildren(); i < n; i++) {
					node.getChild(i).accept(this);
				}
				multistack.peek(name).add(step);
			}
			multistack.pop(name);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			Stack<String> temp = new Stack<>();

			for (Element token : node.getElements()) {
				if (token instanceof ElementConstantDouble) {
					temp.push(((ElementConstantDouble) token).asText());
				}

				if (token instanceof ElementConstantInteger) {
					temp.push(((ElementConstantInteger) token).asText());
				}

				if (token instanceof ElementString) {
					temp.push(((ElementString) token).asText());
				}

				if (token instanceof ElementVariable) {
					String value = (((ElementVariable) token).asText());
					temp.push(String.valueOf(multistack.peek(value).getValue()));
				}

				if (token instanceof ElementOperator) {
					String operator = ((ElementOperator) token).asText();
					Object argument = new ValueWrapper(temp.pop()).getValue();
					ValueWrapper wrapper = new ValueWrapper(temp.pop());

					switch (operator) {
					case "+":
						wrapper.add(argument);
						break;
					case "-":
						wrapper.subtract(argument);
						break;
					case "*":
						wrapper.multiply(argument);
						break;
					case "/":
						wrapper.divide(argument);
						break;
					default:
						throw new IllegalArgumentException("Unsupported operation!");
					}

					temp.push(String.valueOf(wrapper.getValue()));
				}

				if (token instanceof ElementFunction) {
					String funName = token.asText().substring(1);

					switch (funName) {
					case "sin":

						ValueWrapper value = new ValueWrapper(temp.pop());
						Double x = Double.parseDouble(value.getValue().toString());
						System.out.println(String.valueOf(Math.sin(x)));
						temp.push(String.valueOf(Math.sin(x)));
						break;
					case "dmft":
						temp.push(new DecimalFormat(temp.pop().toString())
								.format(Double.parseDouble(temp.pop().toString())));
						break;
					case "dup":
						temp.push(temp.peek());
						break;
					case "swap":
						String first = temp.pop();
						String second = temp.pop();

						temp.push(first);
						temp.push(second);
						break;
					case "setMimeType":
						requestContext.setMimeType(temp.pop().toString());
						break;
					case "paramGet":
						getparam("param", temp);
						break;
					case "pparamGet":
						getparam("persistant", temp);
					case "pparamSet":
						if (temp.size() >= 2) {
							String name = temp.pop().toString();
							String val = temp.pop().toString();

							requestContext.setPersistentParameter(name, val);
						}
						break;
					case "pparamDel":
						requestContext.removePersistentParameter(temp.pop().toString());
						break;
					case "tparamGet":
						getparam("temporary", temp);
						break;
					case "tparamSet":
						String nameTemporary = temp.pop().toString();
						String valTemporary = temp.pop().toString();

						requestContext.setTemporaryParameter(nameTemporary, valTemporary);
						break;
					case "tparamDel":
						requestContext.removeTemporaryParameter(temp.pop().toString());
						break;
					}
				}
			}

			if (temp.size() != 0) {
				Stack<String> secondStack = new Stack<>();

				while (temp.size() != 0) {
					secondStack.add(temp.pop());
				}

				for (String object : secondStack) {
					try {
						requestContext.write(object.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		private void getparam(String string, Stack<String> temp) {
			String tempValue = temp.pop();
			String value;

			if (string.equals("temporary"))
				value = requestContext.getTemporaryParameter(temp.pop().toString());
			else if (string.equals("persistant"))
				value = requestContext.getPersistentParameter(temp.pop().toString());
			else
				value = requestContext.getParameters(temp.pop().toString());

			temp.push((value == null) ? tempValue : value);
		}

		// private void makeOperation(Object first, Object second, ElementOperator
		// operator) {
		//
		// }

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for (int i = 0, length = node.numberOfChildren(); i < length; i++) {
				node.getChild(i).accept(this);
			}
		}

	};

	/**
	 * Constructor initializes new {@link SmartScriptEngine}
	 * 
	 * @param documentNode
	 *            - document node
	 * @param requestContext
	 *            - request
	 */
	public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
		this.documentNode = documentNode;
		this.requestContext = requestContext;
	}

	/**
	 * Method executes action of document node visiting
	 */
	public void execute() {
		documentNode.accept(visitor);
	}
}
