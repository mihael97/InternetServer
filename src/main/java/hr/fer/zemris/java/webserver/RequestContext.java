package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class represents one {@link RequestContext} with header and output stream
 * where we store our data
 * 
 * @author Mihael
 *
 */
public class RequestContext {
	/**
	 * Output stream where we store out data
	 */
	private OutputStream outputStream;
	/**
	 * Char set which is use in {@link OutputStream} writing
	 */
	private Charset charset;
	/**
	 * Name of current encoding
	 */
	private String encoding = "UTF-8";
	/**
	 * Status code
	 */
	private int statusCode = 200;
	/**
	 * Request status text
	 */
	private String statusText = "OK";
	/**
	 * Request mime type
	 */
	private String mimeType = "text/html";
	/**
	 * Map containes all parameters
	 */
	private Map<String, String> parameters;
	/**
	 * Map contains temporary parameters
	 */
	private Map<String, String> temporaryParameters;
	/**
	 * Map contains persistent parameters
	 */
	private Map<String, String> persistentParameters;
	/**
	 * List containes all output cookies
	 */
	private List<RCCookie> outputCookies;
	/**
	 * Shows if header is generated
	 */
	private boolean headerGenerated = false;
	/**
	 * Dispatcher
	 */
	private IDispatcher dispatcher;

	/**
	 * Constructor initializes new request
	 * 
	 * @param outputStream
	 *            - stream where we store out data
	 * @param parameters
	 *            - parameters
	 * @param persistentParameters
	 *            - persistent parameters
	 * @param outputCookies
	 *            - output cookies
	 * 
	 * @throws NullPointerException
	 *             - if output stream is null
	 */
	public RequestContext(OutputStream outputStream, Map<String, String> parameters,
			Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
		this.outputStream = Objects.requireNonNull(outputStream);
		this.parameters = parameters == null ? new LinkedHashMap<>() : parameters;
		this.parameters.forEach((i, j) -> System.out.println(i + "=>" + j));
		this.persistentParameters = persistentParameters == null ? new LinkedHashMap<>() : persistentParameters;
		this.outputCookies = outputCookies == null ? new ArrayList<>() : outputCookies;
		temporaryParameters = new LinkedHashMap<>();
	}

	/**
	 * Constructor initializes new request
	 * 
	 * @param outputStream
	 *            - stream where we store out data
	 * @param parameters
	 *            - parameters
	 * @param persistentParameters
	 *            - persistent parameters
	 * @param outputCookies
	 *            - output cookies
	 * @param temporaryParameters
	 *            - temporary parameters
	 * @param dispatcher
	 *            - {@link IDispatcher} dispatcher
	 * 
	 * @throws NullPointerException
	 *             - if output stream is null
	 */
	public RequestContext(OutputStream outputStream, Map<String, String> parameters,
			Map<String, String> persistentParameters, List<RCCookie> outputCookies,
			Map<String, String> temporaryParameters, IDispatcher dispatcher) {
		this(outputStream, parameters, persistentParameters, outputCookies);
		this.temporaryParameters = temporaryParameters;
		this.dispatcher = dispatcher;
	}

	/**
	 * Returns map with temporary parameters
	 * 
	 * @return temporary parameters map
	 */
	public Map<String, String> getTemporaryParameters() {
		return temporaryParameters;
	}

	/**
	 * Method replaces temporary parameters map with new one
	 * 
	 * @param temporaryParameters
	 *            - new temporary parameters map
	 */
	public void setTemporaryParameters(Map<String, String> temporaryParameters) {
		this.temporaryParameters = temporaryParameters;
	}

	/**
	 * Method returns map with persistent parameters
	 * 
	 * @return map with persistent parameters
	 */
	public Map<String, String> getPersistentParameters() {
		return persistentParameters;
	}

	/**
	 * Method replaces persistent parameters map with new one
	 * 
	 * @param persistentParameters
	 *            - new persistent parameters map
	 */
	public void setPersistentParameters(Map<String, String> persistentParameters) {
		this.persistentParameters = persistentParameters;
	}

	/**
	 * Method returns parameters map
	 * 
	 * @return parameters map
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Method replaces new output stream
	 * 
	 * @param outputStream
	 *            - new output stream
	 */
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * Method sets current encoding. Encoding cannot be changed after header is
	 * generated
	 * 
	 * @param encoding
	 *            - new encoding
	 */
	public void setEncoding(String encoding) {
		checkHeader();
		this.encoding = encoding;
	}

	/**
	 * Method sets current status code. Status code cannot be changed after header
	 * is generated
	 * 
	 * @param statusCode
	 *            - new status code
	 */
	public void setStatusCode(int statusCode) {
		checkHeader();
		this.statusCode = statusCode;
	}

	/**
	 * Method sets status text. Status text cannot be changed after header is
	 * generated
	 * 
	 * @param statusText
	 *            - new status text
	 */
	public void setStatusText(String statusText) {
		checkHeader();
		this.statusText = statusText;
	}

	/**
	 * Method sets mime type.Mime type cannot be changed after header is generated
	 * 
	 * @param mimeType
	 *            - new mime type
	 */
	public void setMimeType(String mimeType) {
		checkHeader();
		this.mimeType = mimeType;
	}

	/**
	 * Method returns value stored in pair with argument
	 * 
	 * @param name
	 *            - key
	 * @return value stored in pair with argument
	 */
	public String getParameter(String name) {

		return parameters.get(name);
	}

	/**
	 * Method returns unmodifiable set of parameters keys
	 * 
	 * @return unmodifiable set of keys
	 */
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}

	/**
	 * Method sets output cookies. If header is already generated,cookies cannot be
	 * changed
	 * 
	 * @param outputCookies
	 *            - new output cookies
	 */
	public void setOutputCookies(List<RCCookie> outputCookies) {
		checkHeader();
		this.outputCookies = Objects.requireNonNull(outputCookies);
	}

	/**
	 * Method returns value stored in pair with argument in persistent parameter map
	 * 
	 * @param name
	 *            - key
	 * @return value stored in pair with argument
	 */
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}

	/**
	 * Method returns unmodifiable set with persistent parameter map keys
	 * 
	 * @return unmodifiable string set
	 */
	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}

	/**
	 * Method puts inside persistent parameter map pair with given key and value
	 * 
	 * @param name
	 *            - key of pair
	 * @param value
	 *            - value of pair
	 */
	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}

	/**
	 * Method removes persistent parameter with given key
	 * 
	 * @param name
	 *            - key of pair we want to remove
	 */
	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}

	/**
	 * Method returns value stored with argument key
	 * 
	 * @param name
	 *            - key
	 * @return value stored in pair with key
	 */
	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}

	/**
	 * Method returns unmodifiable set with temporary parameters keys
	 * 
	 * @return unmodifiable String set
	 */
	public Set<String> getTemporaryParameterNames() {
		return Collections.unmodifiableSet(temporaryParameters.keySet());
	}

	/**
	 * Method puts value in map with temporary parameters
	 * 
	 * @param name
	 *            - key
	 * @param value
	 *            - value we want to store
	 */
	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}

	/**
	 * Method removes temporary parameter with key as argument
	 * 
	 * @param name
	 *            - key in map
	 */
	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}

	/**
	 * Method returns current dispatcher
	 * 
	 * @return {@link IDispatcher} dispatcher
	 */
	public IDispatcher getDispatcher() {
		return dispatcher;
	}

	/**
	 * Method writes data in output stream. During first call of method,file header
	 * will be generated
	 * 
	 * @param data
	 *            - bytes we want to store
	 * @return this {@link RequestContext}
	 * @throws IOException
	 *             - problems during writing
	 */
	public RequestContext write(byte[] data) throws IOException {
		writeHeader();
		outputStream.write(data);
		return this;
	}

	/**
	 * Method accepts string and delegates work to constructor with byte array as
	 * argument
	 * 
	 * @param text
	 *            - text we want to store in output stream
	 * @return this {@link RequestContext}
	 * @throws IOException
	 *             - problems during writing
	 */
	public RequestContext write(String text) throws IOException {
		return write(text.getBytes(encoding));
	}

	/**
	 * After first call of any <code>write</code> function,methods generates header
	 * with informations
	 */
	private void writeHeader() {
		if (!headerGenerated) {
			headerGenerated = true;
			charset = Charset.forName(encoding);

			StringBuilder header = new StringBuilder();

			header.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
			setMime(header);
			setCookies(header);

			header.append("\r\n");

			try {
				outputStream.write(header.toString().getBytes(charset));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * If cookies exist,method appends current header with informations about all
	 * cookies
	 * 
	 * @param header
	 *            - stored header
	 */
	private void setCookies(StringBuilder header) {
		if (outputCookies.size() != 0) {
			for (RCCookie cookie : outputCookies) {
				header.append(" " + cookie.getName()).append("\"").append(cookie.getValue()).append("\";");

				if (cookie.getDomain() != null) {
					header.append(" Domain=").append(cookie.domain).append(";");
				}

				if (cookie.getPath() != null) {
					header.append(" Path=").append(cookie.path).append(";");
				}

				if (cookie.getMaxAge() != null) {
					header.append(" Max-Age=").append(cookie.maxAge).append(";");
				}

				if (cookie.http == true) {
					header.append(" HttpOnly;");
				}
			}

			header.append("\r\n");
		}
	}

	/**
	 * Method appends header with mime properties. If <code>mimeType</code> starts
	 * with <code>"text/"</code>,method will append encoding type
	 * 
	 * @param header
	 *            - string builder where generated header is stored
	 */
	private void setMime(StringBuilder header) {
		header.append("Content-Type: ").append(mimeType);
		if (mimeType.startsWith("text/")) {
			header.append("; charset=").append(encoding);
		}
		header.append("\r\n");
	}

	/**
	 * Method checks if header is already generated before we want to set:
	 * <ul>
	 * <li>encoding</li>
	 * <li>statusCode</li>
	 * <li>statusText</li>
	 * <li>mimeType</li>
	 * <li>outputCookies</li>
	 * </ul>
	 * 
	 * If header is generated,method will throw {@link RuntimeException}
	 * 
	 * @throws RuntimeException
	 *             - if header is generated
	 */
	private void checkHeader() {
		if (headerGenerated) {
			throw new RuntimeException("Properties cannot be changed after header is generated!");
		}
	}

	/**
	 * Method adds {@link RCCookie} in cookie list. If {@link RCCookie} is
	 * null,method doesn't do anything
	 * 
	 * @param cookie
	 *            - {@link RCCookie} we want to add
	 */
	public void addRCCookie(RCCookie cookie) {
		if (cookie != null) {
			this.outputCookies.add(cookie);
		}
	}

	/**
	 * Public static class for cookie implementation
	 * 
	 * @author Mihael
	 *
	 */
	public static class RCCookie {
		/**
		 * Cookie name
		 */
		private String name;
		/**
		 * Cookie value
		 */
		private String value;
		/**
		 * Cookie domain
		 */
		private String domain;
		/**
		 * Cookie path
		 */
		private String path;
		/**
		 * Cookie max age
		 */
		private Integer maxAge;
		/**
		 * Represents if cookie if http only
		 */
		private boolean http;

		/**
		 * Constructor for new {@link RCCookie} instance
		 * 
		 * @param name
		 *            - name
		 * @param value
		 *            - value
		 * @param maxAge
		 *            - maximal age
		 * @param domain
		 *            - domain
		 * @param path
		 *            - path
		 * @param http
		 *            - flag if cookie if http only
		 * 
		 * @throws NullPointerException
		 *             - if name or value are <code>null</code>
		 */
		public RCCookie(String name, String value, Integer maxAge, String domain, String path, boolean http) {
			this.name = Objects.requireNonNull(name);
			this.value = Objects.requireNonNull(value);
			this.maxAge = maxAge;
			this.path = path;
			this.http = http;
		}

		/**
		 * Constructor for new {@link RCCookie} instance. Sets property for HTTP only
		 * cookie to <code>false</code>
		 * 
		 * @param name
		 *            - name
		 * @param value
		 *            - value
		 * @param maxAge
		 *            - maximal age
		 * @param domain
		 *            - domain
		 * @param path
		 *            - path
		 * 
		 * @throws NullPointerException
		 *             - if name or value are <code>null</code>
		 */
		public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
			this(name, value, maxAge, domain, path, false);
		}

		/**
		 * Method returns name
		 * 
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Method returns cookie value
		 * 
		 * @return cookie value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Method return cookie domain
		 * 
		 * @return cookie domain
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * Method returns cookie path
		 * 
		 * @return cookie path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Method returns cookie maximal age
		 * 
		 * @return cookie maximal age
		 */
		public Integer getMaxAge() {
			return maxAge;
		}

		/**
		 * Method returns if cookie if HTTP only
		 * 
		 * @return <code>true</code> if cookie i HTTP only,otherwise <code>false</code>
		 */
		public boolean isHttp() {
			return http;
		}

	}

}
