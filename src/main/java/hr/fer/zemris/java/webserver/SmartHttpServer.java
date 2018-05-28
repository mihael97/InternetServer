package hr.fer.zemris.java.webserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.SequenceInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.lang.model.type.ReferenceType;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * Class represents implementation of basic {@link SmartHttpServer}
 * 
 * @author Mihael
 *
 */
public class SmartHttpServer {
	private String address;
	private String domainName;
	private int port;
	private int workerThreads;
	private int sessionTimeout;
	private Map<String, String> mimeTypes = new HashMap<String, String>();
	private ServerThread serverThread;
	private ExecutorService threadPool;
	private Path documentRoot;
	private boolean serverActive = false;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Argument count must be 1 but is " + args.length);
			return;
		}

		SmartHttpServer server = new SmartHttpServer(args[0]);
		server.start();

		Scanner sc = new Scanner(System.in);

		while (true) {
			if (sc.nextLine().equals("end")) {
				break;
			}
		}

		server.stop();
		sc.close();
	}

	public SmartHttpServer(String configFileName) {
		Properties file = new Properties();

		try {
			file.load(Files.newInputStream(Paths.get(configFileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	}

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

	protected synchronized void start() {
		// … start server thread if not already running …
		// … init threadpool by Executors.newFixedThreadPool(...); …
		threadPool = Executors.newFixedThreadPool(workerThreads);

		if (serverThread == null) {
			serverThread = new ServerThread();
			serverThread.setDaemon(true);
		}

		if (!serverThread.isAlive()) {
			serverThread.start();
			serverActive = true;
		}
	}

	protected synchronized void stop() {
		// … signal server thread to stop running …
		// … shutdown threadpool …
		serverActive = false;
		threadPool.shutdown();
	}

	protected class ServerThread extends Thread {
		@Override
		public void run() {
			// given in pesudo-code:
			// open serverSocket on specified port
			// while(true) {
			// Socket client = serverSocket.accept();
			// ClientWorker cw = new ClientWorker(client);
			// submit cw to threadpool for execution
			// }

			try (ServerSocket socket = new ServerSocket()) {
				socket.bind(new InetSocketAddress(address, port));

				while (serverActive) {
					System.out.println("TU!");
					Socket clientSocket = socket.accept();
					ClientWorker cw = new ClientWorker(clientSocket);
					threadPool.submit(cw);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ClientWorker implements Runnable {
		private Socket csocket;
		private PushbackInputStream istream;
		private OutputStream ostream;
		private String version;
		private String method;
		private String host;
		private Map<String, String> params = new HashMap<String, String>();
		private Map<String, String> tempParams = new HashMap<String, String>();
		private Map<String, String> permPrams = new HashMap<String, String>();
		private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
		private String SID;

		public ClientWorker(Socket csocket) {
			super();
			this.csocket = csocket;
		}

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

				if (!checkHeader(contest)) {
					return;
				}

				String[] requestedPath = contest[1].split("\\?"); // first part is path and after are parameters
				String path = requestedPath[0].substring(1);
				if (requestedPath.length > 1) { // there is at least one parameter
					parseParameters(requestedPath[1]);
				}

				String extension = path.substring(path.lastIndexOf(".") + 1, path.length());
				setHost(request);

				RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
				rc.setStatusCode(200);
				rc.setMimeType(setMime(extension));
				rc.write(Files.readAllBytes(documentRoot.resolve(path)));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private boolean checkHeader(String[] contest) {
			if (contest.length != 3) {
				writeError(400, "Number of arguments in first line must be 3!");
				return false;
			} else if (!contest[0].toUpperCase().equals("GET")) {
				writeError(400, "Method must be GET!");
				return false;
			} else if (!contest[2].toUpperCase().equals("HTTP/1.0") && !contest[2].toUpperCase().equals("HTTP/1.1")) {
				writeError(400, "HTTP version must be 1.0 or 1.1! but is " + contest[2]);
				return false;
			}

			return true;
		}

		private boolean checkInvalidHeader(String[] info) {
			if (info.length != 3) {
				writeError(400, "Bad request");
				return true;
			}
			method = info[0].toUpperCase();
			if (!method.equals("GET")) {
				writeError(405, "Method not allowed");
				return true;
			}
			version = info[2].toUpperCase();
			if (!version.equals("HTTP/1.1")) {
				writeError(505, "HTTP version not supported");
				return true;
			}
			return false;
		}

		private void setHost(List<String> request) {
			for (String string : request) {
				if (string.startsWith("Host:")) {
					host = string.substring("Host:".length(), string.lastIndexOf(":")).trim();
					break;
				}
			}
		}

		private String setMime(String extension) {
			if (mimeTypes.containsKey(extension)) {
				return mimeTypes.get(extension);
			}

			return "application/octet-stream";
		}

		private void parseParameters(String string) {
			for (String str : string.split("&")) {
				String[] values = str.trim().split("=");
				params.put(values[0], values[1]);
			}
		}

		private void writeError(int i, String string) {
			System.err.println("ERROR! " + string);
			try {
				ostream.write(("HTTP/1.1 " + i + " " + string + "\r\n" + "Server: Smart HTTP server\r\n"
						+ "Content-Type: text/html;charset=UTF-8\r\n" + "Content-Length: 0\r\n"
						+ "Connection: close\r\n" + "\r\n").getBytes(StandardCharsets.US_ASCII));
				ostream.flush();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private List<String> readRequest() throws IOException {
			byte[] request = readRequest(istream);
			if (request == null) {
				writeError(400, "Bad request");
				return null;
			}
			String requestHeader = new String(request, StandardCharsets.US_ASCII);
			List<String> headers = new ArrayList<>();
			String currentLine = null;
			for (String s : requestHeader.split("\n")) {
				if (s.isEmpty())
					break;
				char c = s.charAt(0);
				if (c == 9 || c == 32) {
					currentLine += s;
				} else {
					if (currentLine != null) {
						headers.add(currentLine);
					}
					currentLine = s;
				}
			}
			if (!currentLine.isEmpty()) {
				headers.add(currentLine);
			}
			return headers;
		}

		private byte[] readRequest(PushbackInputStream is) throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int state = 0;
			l: while (true) {
				int b = is.read();
				if (b == -1)
					return null;
				if (b != 13) {
					bos.write(b);
				}
				switch (state) {
				case 0:
					if (b == 13) {
						state = 1;
					} else if (b == 10)
						state = 4;
					break;
				case 1:
					if (b == 10) {
						state = 2;
					} else
						state = 0;
					break;
				case 2:
					if (b == 13) {
						state = 3;
					} else
						state = 0;
					break;
				case 3:
					if (b == 10) {
						break l;
					} else
						state = 0;
					break;
				case 4:
					if (b == 10) {
						break l;
					} else
						state = 0;
					break;
				}
			}
			return bos.toByteArray();
		}
	}
}
