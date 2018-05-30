package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Method implements {@link IWebWorker} and have implementation for dynamically
 * color changing
 * 
 * @author Mihael
 *
 */
public class BgColorWorker implements IWebWorker {

	/**
	 * Method checks if value with key <code>bgcolor</code> is set in parameter map
	 * and then creates new HTML page with appropriate message
	 * 
	 * @param context
	 *            - {@link RequestContext}
	 */
	@Override
	public void processRequest(RequestContext context) throws Exception {
		String value = context.getParameter("bgcolor");

		if (value != null && value.length() == 6) {
			context.setPersistentParameter("bgcolor", value);
			context.write(createHTML(true));
			return;
		}

		context.write(createHTML(false));
	}

	/**
	 * Method creates new HTML file with appropriate message
	 * 
	 * @param changed
	 *            - represents if color is changed or not
	 * @return HTML file in string format
	 */
	private String createHTML(boolean changed) {
		StringBuilder builder = new StringBuilder();

		builder.append("<html><body>");
		builder.append((changed == true) ? "Color has been changed " : "Color has not been changed ");
		builder.append("<a href=\"http://www.localhost.com:5721/index2.html\"><br>Press return</a>");
		builder.append("</body><html>");

		return builder.toString();
	}

}
