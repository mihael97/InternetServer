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
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Class represents executor of parsed tree document
 * 
 * @author Mihael
 *
 */
public class SmartScriptEngine {
	/**
	 * Document node
	 */
	private DocumentNode documentNode;
	/**
	 * Request
	 */
	private RequestContext requestContext;
	/**
	 * Stack where we store out elements
	 */
	private ObjectMultistack multistack = new ObjectMultistack();
	/**
	 * Implementation of visitor with specified method for every type of node
	 */
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

			while (start.numCompare(end) <= 0) {
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
					operation(temp, token);
				}

				if (token instanceof ElementFunction) {
					function(temp, token);
				}
			}

			if (temp.size() != 0) {
				Stack<String> second = new Stack<>();

				while (temp.size() != 0) {
					second.push(temp.pop().toString());
				}

				while (second.size() != 0) {
					try {
						requestContext.write(second.pop().toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void function(Stack<String> temp, Element token) {
			String funName = token.asText().substring(1);

			switch (funName) {
			case "sin":
				String obj = temp.pop();
				Double value = Double.parseDouble(obj);
				temp.push(String.valueOf(Math.sin(value * Math.PI / 180)));
				break;
			case "decfmt":
				temp.push(new DecimalFormat(temp.pop().toString()).format(Double.valueOf(temp.pop())));
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

		private void operation(Stack<String> temp, Element token) {
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
				throw new IllegalArgumentException("Unsupported operation! Operation: " + operator);
			}

			temp.push(String.valueOf(wrapper.getValue()));
		}

		private void getparam(String string, Stack<String> temp) {
			String tempValue = temp.pop();
			String value;

			if (string.equals("temporary")) {
				value = requestContext.getTemporaryParameter(temp.pop().toString());
			} else if (string.equals("persistant")) {
				value = requestContext.getPersistentParameter(temp.pop().toString());
			} else {
				String key = temp.pop().toString();
				value = requestContext.getParameter(key);
			}

			temp.push((value == null) ? tempValue : value);
		}

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
