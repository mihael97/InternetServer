package hr.fer.zemris.java.webserver.workers;

import java.util.Map;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Class implements {@link IWebWorker} and prints all parameters from given
 * {@link RequestContext}
 * 
 * @author Mihael
 *
 */
public class EchoParams implements IWebWorker {

	/**
	 * Method prints all parameters
	 * 
	 * @param context
	 *            - {@link RequestContext}
	 */
	@Override
	public void processRequest(RequestContext context) throws Exception {
		context.write("This table shows all parameters for file!\n\n\n\n");
		context.write("<table>");
		if (context.getParameters().size() != 0) {
			for (Map.Entry<String, String> map : context.getParameters().entrySet()) {
				context.write("<tr><td>" + map.getKey() + "</td><td>" + map.getValue() + "</td></tr>");
			}
		} else {
			context.write("Map is empty!");
		}

		context.write("</table>");
	}

}
