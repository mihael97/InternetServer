package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	/**
	 * Number of port where server is connected
	 */
	private int port;
	/**
	 * Number of worker threads
	 */
	private int workerThreads;
	private int sessionTimeout;
	private Map<String, String> mimeTypes = new HashMap<String, String>();
	private ServerThread serverThread;
	private ExecutorService threadPool;
	private Path documentRoot;
	/**
	 * Flag represents if server is running
	 */
	private boolean activeServer = false;

	/**
	 * Constructor initializes new instance of server
	 * 
	 * @param configFileName
	 *            - path to properties file
	 */
	public SmartHttpServer(String configFileName) {
		Properties file = new Properties();

		try {
			file.load(
					Files.newInputStream(Paths.get(Objects.requireNonNull(configFileName, "String cannot be null!"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Synchronized method starts server
	 */
	protected synchronized void start() {
		// … start server thread if not already running …
		// … init threadpool by Executors.newFixedThreadPool(...); …
		if (activeServer == false) {
			activeServer = true;

			threadPool = Executors.newFixedThreadPool(workerThreads);

			if (serverThread == null) {
				serverThread = new ServerThread();
				serverThread.setDaemon(true);
			}

			if (!serverThread.isAlive()) {
				serverThread.start();
			}

		}
	}

	/**
	 * Synchronized method stops server running
	 */
	protected synchronized void stop() {
		activeServer = false;
		threadPool.shutdown();
	}

	/**
	 * Class extends {@link Thread} and represents server thread. <code>Run</code>
	 * method waits until new request come and after that submits it to
	 * <code>thread Pool</code>
	 * 
	 * @author Mihael
	 *
	 */
	protected class ServerThread extends Thread {
		/**
		 * While server is active,thread waits new request and after submits it to
		 * thread pool
		 */
		@Override
		public void run() {
			try (ServerSocket serverSocket = new ServerSocket(port)) {

				while (activeServer) {
					Socket client = serverSocket.accept();
					ClientWorker cw = new ClientWorker(client);
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
				istream = new PushbackInputStream(csocket.getInputStream());
				ostream = csocket.getOutputStream();

				List<String> request = readRequest();

				// if request in not valid
				if (request == null || request.size() < 1) {
					requestError();
				}

				String firstLine = request.get(0);
				String[] firstLineContest = firstLine.split(" ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void requestError() {
			// TODO Auto-generated method stub

		}

		/**
		 * Method reads whole header
		 * 
		 * @return list of header lines
		 */
		private List<String> readRequest() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
