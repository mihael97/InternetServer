package hr.fer.zemris.java.webserver;

/**
 * Interface provides method for objects that can process request
 * 
 * @author Mihael
 *
 */
public interface IWebWorker {
	/**
	 * Method processes request
	 * 
	 * @param context
	 *            - request
	 * @throws Exception
	 *             - if any exception happen
	 */
	public void processRequest(RequestContext context) throws Exception;
}
