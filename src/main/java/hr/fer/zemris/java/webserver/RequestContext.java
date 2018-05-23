package hr.fer.zemris.java.webserver;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestContext {
	private OutputStream outputStream;
	private Charset charset;
	private String encoding = "UTF-8";
	private int statusCode = 200;
	private String statusText = "OK";
	private String mimeType = "text/html";
	private Map<String, String> parameters;
	private Map<String, String> temporaryParameters;
	private Map<String, String> persistentParameters;
	private List<RCCookie> outputCookies;
	private boolean headerGenerated = false;

	public RequestContext(OutputStream outputStream, Map<String, String> parameters,
			Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
		this.outputStream = Objects.requireNonNull(outputStream);
		this.parameters = parameters == null ? new LinkedHashMap<>() : parameters;
		this.persistentParameters = persistentParameters == null ? new LinkedHashMap<>() : parameters;
		this.outputCookies = outputCookies == null ? new ArrayList<>() : outputCookies;
	}
	
	

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
		this.encoding = encoding;
	}



	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}



	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}



	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}



	public static class RCCookie {
		private String name;
		private String value;
		private String domain;
		private String path;
		private Integer maxAge;

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getDomain() {
			return domain;
		}

		public String getPath() {
			return path;
		}

		public Integer getMaxAge() {
			return maxAge;
		}

	}
}
