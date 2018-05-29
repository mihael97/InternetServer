package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class Home implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {

		context.setTemporaryParameter("background",
				context.getPersistentParameter("background") != null ? context.getPersistentParameter("bgcolor")
						: "7F7F7F");
		context.getDispatcher().dispatchRequest("/private/home.smscr");
	}

}
