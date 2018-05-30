package hr.fer.zemris.java.webserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * Class represents implementation of basic {@link SmartHttpServer}
 * 
 * @author Mihael
 *
 */
public class SmartHttpServer {
	/**
	 * Server address
	 */
	private String address;
	/**
	 * Domain name
	 */
	private String domainName;
	/**
	 * Port to which server is connected
	 */
	private int port;
	/**
	 * Number of worker threads
	 */
	private int workerThreads;
	/**
	 * How long session is active
	 */
	private int sessionTimeout;
	/**
	 * Map with all mimes
	 */
	private Map<String, String> mimeTypes = new HashMap<String, String>();
	/**
	 * Server thread
	 */
	private ServerThread serverThread;
	/**
	 * Thread pool
	 */
	private ExecutorService threadPool;
	/**
	 * Path to root document
	 */
	private Path documentRoot;
	/**
	 * Shows if server is active
	 */
	private boolean serverActive = false;
	/**
	 * Map with all workers
	 */
	private Map<String, IWebWorker> workersMap;

	/**
	 * Map with active sessions
	 */
	private Map<String, SessionMapEntry> sessions = new HashMap<String, SmartHttpServer.SessionMapEntry>();
	/**
	 * Random generator for session sids
	 */
	private Random sessionRandom = new Random();

	/**
	 * Every 5 minutes thread pass trough all sessions and removes inactive ones
	 */
	private Thread sessionCleaner = new Thread(() -> {
		while (serverActive) {
			Iterator<Map.Entry<String, SessionMapEntry>> iterator = sessions.entrySet().iterator();

			while (iterator.hasNext()) {
				SessionMapEntry entry = iterator.next().getValue();

				if (!checkDate(entry.validUntil)) {
					iterator.remove();
				}
			}

			try {
				Thread.sleep(1000 * 5 * 60); // method waits for 5 minutes(300000 miliseconds)
			} catch (InterruptedException ignore) {
			}
		}
	});

	/**
	 * Main program
	 * 
	 * @param args
	 *            - must have 1 argument where arguments is path to properties file
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Argument count must be 1 but is " + args.length);
			return;
		}

		SmartHttpServer server = new SmartHttpServer(args[0]);
		server.start();

		Scanner sc = new Scanner(System.in);
		System.out.println("Server started...");
		while (true) {
			System.out.println("Enter 'end' for stop server: ");
			if (sc.nextLine().equals("end")) {
				break;
			}
		}

		System.out.println("Server stopped!");
		server.stop();
		sc.close();
	}

	/**
	 * Public constructor for server initialization
	 * 
	 * @param configFileName
	 *            - path to server properties file
	 * @throws IOException
	 *             - exception during reading
	 */
	public SmartHttpServer(String configFileName) {
		Properties file = new Properties();

		try {
			file.load(Files.newInputStream(Paths.get(configFileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.address = file.getProperty("server.address");
		this.domainName = file.getProperty("server.domainName");
		this.port = Integer.parseInt(file.getProperty("server.port"));
		this.workerThreads = Integer.parseInt(file.getProperty("server.workerThreads"));
		this.documentRoot = Paths.get(file.getProperty("server.documentRoot"));
		this.sessionTimeout = Integer.parseInt(file.getProperty("session.timeout"));

		// mime loading
		loadMimes(file.getProperty("server.mimeConfig"));

		// worker loading
		loadWorkers(file.getProperty("server.workers"));
	}

	/**
	 * Method loads all workers from file
	 * 
	 * @param property
	 *            - path to worker properties file
	 * 
	 * @throws IOException
	 *             - exception during reading
	 */
	private void loadWorkers(String property) {
		Properties file = new Properties();

		try {
			file.load(Files.newInputStream(Paths.get(property)));

			workersMap = new LinkedHashMap<>();

			for (Object key : file.keySet()) {
				String className = file.getProperty(key.toString());

				workersMap.put(key.toString(), getWorker(className));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method worker class which implements {@link IWebWorker}
	 * 
	 * @param className
	 *            - path to class on disc
	 * @return worker class
	 * 
	 * @throws Exception
	 *             - if any exception appears
	 */
	private IWebWorker getWorker(String className) {
		try {
			Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(className);
			Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
			return (IWebWorker) newObject;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Method loads mimes from file and fills map with extension like keys and
	 * values like file informations
	 * 
	 * @param property
	 *            - path to file
	 * @throws IllegalArgumentException
	 *             - if mime file cannot be loaded
	 */
	private void loadMimes(String property) {
		Properties file = new Properties();

		try {
			file.load(Files.newInputStream(Paths.get(property)));
		} catch (IOException e) {
			throw new IllegalArgumentException("Mime file cannot be loaded!");
		}

		for (Object key : file.keySet()) {
			mimeTypes.put(key.toString(), file.get(key).toString());
		}
	}

	/**
	 * Synchronized method starts server
	 */
	protected synchronized void start() {
		threadPool = Executors.newFixedThreadPool(workerThreads);

		if (serverThread == null) {
			serverThread = new ServerThread();
			serverThread.setDaemon(true);
		}

		if (!serverThread.isAlive()) {
			serverThread.start();
			sessionCleaner.start();
			serverActive = true;
		}
	}

	/**
	 * Synchronized method stops server
	 */
	protected synchronized void stop() {
		serverActive = false;
		sessionCleaner.interrupt();
		threadPool.shutdown();
	}

	/**
	 * Class extends {@link Thread} and represents server thread
	 * 
	 * @author Mihael
	 *
	 */
	protected class ServerThread extends Thread {
		/**
		 * Method is running server until we stop server
		 */
		@Override
		public void run() {

			try {
				ServerSocket socket = new ServerSocket();
				socket.bind(new InetSocketAddress(address, port));

				while (serverActive) {
					Socket clientSocket = socket.accept();
					ClientWorker cw = new ClientWorker(clientSocket);
					threadPool.submit(cw);
				}

				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Class represents structure which stores information about client and his
	 * cookies with this session
	 * 
	 * @author Mihael
	 *
	 */
	private static class SessionMapEntry {
		/**
		 * Unique random generated sid
		 */
		String sid;
		/**
		 * Host
		 */
		String host;
		/**
		 * Time in second until session is active
		 */
		long validUntil;
		/**
		 * Map with cookies
		 */
		Map<String, String> map;
	}

	/**
	 * Class implements {@link Runnable} and {@link IDispatcher} and represents
	 * server client
	 * 
	 * @author Mihael
	 *
	 */
	private class ClientWorker implements Runnable, IDispatcher {
		/**
		 * Client socket
		 */
		private Socket csocket;
		/**
		 * Input stream
		 */
		private PushbackInputStream istream;
		/**
		 * Output stream
		 */
		private OutputStream ostream;
		/**
		 * HTTP version
		 */
		private String version;
		/**
		 * Method name
		 */
		private String method;
		/**
		 * Host name
		 */
		private String host;
		/**
		 * Map with parameters
		 */
		private Map<String, String> params = new HashMap<String, String>();
		/**
		 * Map with temporary parameters
		 */
		private Map<String, String> tempParams = new HashMap<String, String>();
		/**
		 * Map with
		 */
		private Map<String, String> permPrams = new HashMap<String, String>();
		/**
		 * List with output cookies
		 */
		private List<RCCookie> outputCookies = new ArrayList<RCCookie>();
		/**
		 * SID
		 */
		private String SID;
		/**
		 * Request context
		 */
		private RequestContext context = null;

		/**
		 * Constructor initialize new {@link ClientWorker}
		 * 
		 * @param csocket
		 *            - client socket
		 */
		public ClientWorker(Socket csocket) {
			super();
			this.csocket = csocket;
		}

		/**
		 * Method performs request processing
		 * 
		 * @throws IOException
		 *             - exception during writing
		 */
		@Override
		public void run() {
			try {
				istream = new PushbackInputStream(csocket.getInputStream());// input stream
				ostream = csocket.getOutputStream(); // output stream

				List<String> request = readRequest(); // request content

				if (request == null || request.size() < 1) {
					writeError(400, "Request content is null or size is lower than 1!");
					return;
				}

				String firstLine = request.get(0); // first line,stored method,version and requested path
				String[] contest = firstLine.split(" "); // contest form first line

				method = contest[0].toUpperCase().trim();
				version = contest[2].toUpperCase().trim();

				if (contest.length != 3) {
					writeError(400, "Number of arguments in first line must be 3!");
					return;
				} else if (!method.equals("GET")) {
					writeError(400, "Method must be GET!");
					return;
				} else if (!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
					writeError(400, "HTTP version must be 1.0 or 1.1! but is " + contest[2]);
					return;
				}

				String[] requestedPath = contest[1].split("\\?"); // first part is path and after are parameters
				String path = requestedPath[0].substring(1);
				setHost(request);
				checkSession(request);
				if (requestedPath.length > 1) { // there is at least one parameter
					parseParameters(requestedPath[1]);
				}

				internalDispatchRequest(path, true);

			} catch (NoSuchFileException ignore) {
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					csocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		/**
		 * Synchronized method checks cookies and current session
		 *
		 * @param request
		 *            - header lines
		 */
		private synchronized void checkSession(List<String> request) {
			String sidCandidate = null;

			for (String line : request) {
				if (line.trim().startsWith("Cookie:")) {
					for (String cookie : line.substring("Cookie: ".length()).split(";")) {
						String[] cookieElement = cookie.split("=");

						if (cookieElement[0].equals("sid")) {
							sidCandidate = cookieElement[1].replace("\"", "");
						}
					}
				}
			}

			if (sidCandidate != null) {
				SessionMapEntry entry = sessions.get(sidCandidate);

				if (entry != null) {
					if (entry.host == null && host == null || entry.host.equals(host)) {
						if (checkDate(entry.validUntil)) {
							entry.validUntil = System.currentTimeMillis() / 1000 + sessionTimeout;
							setSeedAndPerm(entry);
							return;
						}

						sessions.remove(sidCandidate);
					}
				}
			}

			createEntry();
		}

		/**
		 * Method creates new {@link SessionMapEntry} entry
		 */
		private void createEntry() {
			String uniqueSid = getSID();

			SessionMapEntry entry = new SessionMapEntry();
			entry.sid = uniqueSid;
			entry.validUntil = System.currentTimeMillis() / 1000 + sessionTimeout;
			entry.host = this.host;
			entry.map = new ConcurrentHashMap<>(); // map must be thread safe
			entry.map.put("sid", uniqueSid);

			// we are filling map with existing cookies
			for (RCCookie cookie : outputCookies) {
				entry.map.put(cookie.getName(), cookie.getValue());
			}

			sessions.put(uniqueSid, entry);
			outputCookies.add(new RCCookie("sid", uniqueSid, null, this.host, "/", true));

			setSeedAndPerm(entry);
		}

		/**
		 * Method sets current id and perm map to same type entry values
		 * 
		 * @param entry
		 *            - {@link SessionMapEntry}
		 */
		private void setSeedAndPerm(SessionMapEntry entry) {
			SID = entry.sid;
			permPrams = entry.map;
		}

		/**
		 * Method calculates unique key(sid). Every sid is string made by 20 random
		 * generated upper case characters
		 *
		 * @return unique sid
		 */
		private String getSID() {
			char[] forReturn = new char[20];

			do {
				for (int i = 0; i < forReturn.length; i++) {
					forReturn[i] = (char) (65 + sessionRandom.nextInt(26)); // upper case characters have integer values
																			// between 65 and 90 in ASCII
				}
			} while (sessions.containsKey(new String(forReturn)));

			return new String(forReturn);
		}

		/**
		 * Method sets host name
		 * 
		 * @param request
		 *            - header lines
		 */
		private void setHost(List<String> request) {
			for (String string : request) {
				if (string.startsWith("Host:")) {
					host = string.substring("Host:".length(), string.lastIndexOf(":")).trim();
					break;
				}
			}
		}

		/**
		 * Method returns mime for given extension
		 * 
		 * @param extension
		 *            - file extension
		 * @return mime for specific extension
		 */
		private String setMime(String extension) {
			if (mimeTypes.containsKey(extension)) {
				return mimeTypes.get(extension);
			}

			return "application/octet-stream";
		}

		/**
		 * Method parses part of string with parameters and fills parameter map with
		 * values
		 * 
		 * @param string
		 *            - string with parameters
		 */
		private void parseParameters(String string) {
			for (String str : string.split("&")) {
				String[] values = str.trim().split("=");
				params.put(values[0], values[1]);
			}
		}

		/**
		 * When error appeared,method writes error informations into header
		 * 
		 * @param i
		 *            - error code
		 * @param string
		 *            error description
		 * 
		 * @throws IOException
		 *             - exception during writing
		 */
		private void writeError(int i, String string) {
			try {
				ostream.write(("HTTP/1.1 " + i + " " + string + "\r\n" + "Server: Smart HTTP server\r\n"
						+ "Content-Type: text/html;charset=UTF-8\r\n" + "Content-Length: 0\r\n"
						+ "Connection: close\r\n" + "\r\n").getBytes(StandardCharsets.US_ASCII));
				ostream.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Method creates list of request lines
		 * 
		 * @return request lines
		 * @throws IOException
		 *             - if exception during reading appears
		 */
		private List<String> readRequest() throws IOException {
			byte[] byteArray = readBytes(istream);

			if (byteArray == null) {
				writeError(400, "Byte array form request is not valid!");
				return null;
			}

			List<String> header = new ArrayList<>();
			String builder = null;

			for (String string : new String(byteArray).split("\n")) {
				if (string.length() == 0)
					break;

				if (string.charAt(0) == ' ' || string.charAt(0) == '\r') {
					builder += string;
				} else {
					if (builder == null) {
						header.add(string);
					}

					builder = string;
				}
			}

			if (builder.length() != 0) {
				header.add(builder);
			}

			return header;
		}

		/**
		 * Method reads all bytes from input stream
		 * 
		 * @param is
		 *            - input stream
		 * @return bytes form stream
		 * @throws IOException
		 *             - error during reading
		 */
		private byte[] readBytes(PushbackInputStream is) throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int state = 0;

			while (true) {
				int b = istream.read();
				if (b == -1)
					return null;
				if (b != 13)
					bos.write(b);

				if (state == 0) {
					if (b == 13)
						state = 1;
					else if (b == 10)
						state = 4;

				} else if (state == 1) {
					if (b == 10)
						state = 2;
					else
						state = 0;

				} else if (state == 2) {
					if (b == 13)
						state = 3;
					else
						state = 0;

				} else if (state == 3) {
					if (b == 10)
						break;
					else
						state = 0;

				} else if (state == 4) {
					if (b == 10)
						break;
					else
						state = 0;
				}
			}
			return bos.toByteArray();
		}

		/**
		 * Method sets boolean flag to <code>false</code> and delegates work to internal
		 * method
		 * 
		 * @param urlPath
		 *            - path
		 */
		@Override
		public void dispatchRequest(String urlPath) throws Exception {
			internalDispatchRequest(urlPath, false);
		}

		/**
		 * Method dispatch request
		 * 
		 * @param urlPath
		 *            - path
		 * @param flag
		 *            - shows if dispatch request is from inside program
		 */
		private void internalDispatchRequest(String urlPath, boolean flag) {
			if (this.context == null) {
				this.context = new RequestContext(ostream, params, permPrams, outputCookies, tempParams, this);
			}

			if (workersMap.containsKey("/" + urlPath)) {
				try {
					workersMap.get("/" + urlPath).processRequest(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			if (urlPath.equals("/private") || urlPath.startsWith("/private/")) {
				if (flag == true) {
					writeError(404, "Direct access is not allowed!");
					return;
				}
				executeSMSCR("webroot" + urlPath);
				return;
			}

			if (urlPath.contains("ext/")) {
				try {
					String className = "hr.fer.zemris.java.webserver.workers." + urlPath.substring("ext/".length());
					executeWorker(className);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (urlPath.endsWith(".smscr") && flag == true) {
				executeSMSCR("webroot/" + urlPath);
				return;
			}

			if (urlPath.equals("favicon.ico"))
				return;

			String extension = urlPath.substring(urlPath.lastIndexOf(".") + 1, urlPath.length());

			RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
			rc.setStatusCode(200);
			rc.setMimeType(setMime(extension));
			byte[] file;
			try {
				file = Files.readAllBytes(documentRoot.resolve(urlPath));
				rc.setLength((long) file.length);
				rc.write(file);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Method executes scripts with <code>.smscr</code> extension
		 * 
		 * @param path
		 *            - path to script
		 * 
		 */
		private void executeSMSCR(String path) {
			String file;
			try {
				file = new String(Files.readAllBytes(Paths.get(path)));
				SmartScriptParser parser = new SmartScriptParser(file);
				SmartScriptEngine engine = new SmartScriptEngine(parser.getDocumentNode(),
						new RequestContext(ostream, params, permPrams, outputCookies, tempParams, this));
				engine.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Method executes worker class
		 * 
		 * @param className
		 *            - worker class name
		 */
		private void executeWorker(String className) {
			try {
				getWorker(className).processRequest(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method checks if entry must be removed because his time expired
	 * 
	 * @param validUntil
	 *            - time when entry become inactive
	 * @return <code>true</code> if entry can still be active,otherwise
	 *         <code>false</code>
	 */
	private boolean checkDate(long validUntil) {
		return validUntil > System.currentTimeMillis() / 1000;
	}
}
