package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Class represents worker which sets temporary parameter for color to value
 * stored in persistent parameter map with key <code>bgcolor</code>. If there is
 * not stored value,new value will be <code>#7f7f7f</code>
 * 
 * @author Mihael
 *
 */
public class Home implements IWebWorker {

	/**
	 * Method performs temporary parameter for color setting
	 * 
	 * @param context
	 *            - request context
	 */
	@Override
	public void processRequest(RequestContext context) throws Exception {

		context.setTemporaryParameter("background",
				context.getPersistentParameter("bgcolor") != null ? context.getPersistentParameter("bgcolor")
						: "7F7F7F");
		context.getDispatcher().dispatchRequest("/private/home.smscr");
	}

}
