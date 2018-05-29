package hr.fer.zemris.java.webserver.workers;

import java.awt.Color;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class BgColorWorker implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String value = context.getParameter("bgcolor");

		if (value != null && value.length() == 6) {
			context.setPersistentParameter("background", "#"+value);
			System.out.println("U BG color "+context.getPersistentParameters().containsKey("background"));
			System.out.println(Color.decode("#" + value).toString());
			System.out.println("Color has been changed!");
			context.getDispatcher().dispatchRequest("/index2.html");
			return;
		}

		System.err.println("Color is not changed!");
		context.getDispatcher().dispatchRequest("/index2.html");
	}

}
