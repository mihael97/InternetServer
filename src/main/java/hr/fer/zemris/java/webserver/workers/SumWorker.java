package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Class implements {@link IWebWorker} and presents calculator of two digits
 * 
 * @author Mihael
 *
 */
public class SumWorker implements IWebWorker {

	/**
	 * Method gets calculates result of addition and puts it with argument to map
	 * with temporary parameters
	 * 
	 * @param context
	 *            - request context
	 */
	@Override
	public void processRequest(RequestContext context) throws Exception {
		int a, b;

		a = getParameter("a", context);
		b = getParameter("b", context);

		context.setTemporaryParameter("a", String.valueOf(a));
		context.setTemporaryParameter("b", String.valueOf(b));
		context.setTemporaryParameter("zbroj", String.valueOf(a + b));

		context.getDispatcher().dispatchRequest("/private/calc.smscr");
	}

	/**
	 * Method gets from parameter map. If parameter doesn't exist,if strign is
	 * <code>a</code> method will return 1 and if string is <code>b</code> method
	 * will return 2.
	 * 
	 * @param string
	 *            - variable we want,must be 'a' or 'b'
	 * @param context
	 *            - request context where parameters are stored
	 * @return int value of parameter
	 * 
	 * @throws IllegalArgumentException
	 *             - if we want value of unsupported variable
	 */
	private int getParameter(String string, RequestContext context) {
		int forReturn;
		try {
			forReturn = Integer.parseInt(context.getParameter(string));
		} catch (NumberFormatException e) {
			if (string.equals("a")) {
				forReturn = 1;
			} else if (string.equals("b")) {
				forReturn = 2;
			} else {
				throw new IllegalArgumentException("Parameter name must be a or b!");
			}
		}

		return forReturn;
	}

}
