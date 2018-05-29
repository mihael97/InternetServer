package hr.fer.zemris.java.webserver;

/**
 * Interface provides method that every dispatcher must have
 * 
 * @author Mihael
 *
 */
public interface IDispatcher {
	/**
	 * In specific cases,method executes request
	 * @param urlPath - path to file
	 * @throws Exception - if any exception appears
	 */
	void dispatchRequest(String urlPath) throws Exception;
}
