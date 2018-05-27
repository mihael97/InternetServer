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
		this.persistentParameters = persistentParameters == null ? new LinkedHashMap<>() : parameters;
		this.outputCookies = outputCookies == null ? new ArrayList<>() : outputCookies;
		temporaryParameters=new LinkedHashMap<>();
	}

	/**
	 * Returns map with temporary parameters
	 * 
	 * @return temporary parameters map
	 */
	public Map<String, String> getTemporaryParameters() {
		return temporaryParameters;
	}

	public void setTemporaryParameters(Map<String, String> temporaryParameters) {
		this.temporaryParameters = temporaryParameters;
	}

	public Map<String, String> getPersistentParameters() {
		return persistentParameters;
	}

	public void setPersistentParameters(Map<String, String> persistentParameters) {
		this.persistentParameters = persistentParameters;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void setEncoding(String encoding) {
		checkHeader();
		this.encoding = encoding;
	}

	public void setStatusCode(int statusCode) {
		checkHeader();
		this.statusCode = statusCode;
	}

	public void setStatusText(String statusText) {
		checkHeader();
		this.statusText = statusText;
	}

	public void setMimeType(String mimeType) {
		checkHeader();
		this.mimeType = mimeType;
	}

	public String getParameters(String name) {
		return parameters.get(name);
	}

	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}

	public void setOutputCookies(List<RCCookie> outputCookies) {
		checkHeader();
		this.outputCookies = Objects.requireNonNull(outputCookies);
	}

	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}

	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}

	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}

	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}

	public String getTemporaryParameter(String name) {
		return temporaryParameters.remove(name);
	}

	public Set<String> getTemporaryParameterNames() {
		return Collections.unmodifiableSet(temporaryParameters.keySet());
	}

	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}

	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}

	public RequestContext write(byte[] data) throws IOException {
		writeHeader();
		outputStream.write(data);
		return this;
	}

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
				// TODO Auto-generated catch block
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
	 * Public static for cookie implementation
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
		 * 
		 * @throws NullPointerException
		 *             - if name or value are <code>null</code>
		 */
		public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
			this.name = Objects.requireNonNull(name);
			this.value = Objects.requireNonNull(value);
			this.maxAge = maxAge;
			this.path = path;
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

	}

}
