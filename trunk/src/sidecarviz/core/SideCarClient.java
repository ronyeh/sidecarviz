package sidecarviz.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import papertoolkit.util.DebugUtils;

public class SideCarClient {

	private String machineName;
	private int portNumber;
	private Thread socketThread;
	private Socket clientSocket;
	private boolean exitFlag = false;
	private SideCarServer commandHandler;

	public SideCarClient(String serverName, int port) {
		machineName = serverName;
		portNumber = port;
		socketThread = new Thread(new Runnable() {
			public void run() {
				try {
					final BufferedReader br = setupSocketAndReader();
					String line = null;
					while ((line = br.readLine()) != null) {
						// print the text of the command / args to the console

						if (commandHandler != null) {
							commandHandler.handleCommand(line);
						} else {
							DebugUtils.println("Unhandled Command: " + line);
						}

						if (exitFlag) {
							break;
						}
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		socketThread.start();
	}

	
	/**
	 * Disconnect from the server.
	 */
	public synchronized void exit() {
		exitFlag = true;
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedReader setupSocketAndReader() throws UnknownHostException, IOException {
		// DebugUtils.println("Trying to connect to " + machineName + ":" + portNumber);
		final InetAddress addr = InetAddress.getByName(machineName);
		clientSocket = new Socket(machineName, portNumber);
		final InputStream inputStream = clientSocket.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		return br;
	}

	public void setCommandHandler(SideCarServer sideCarServer) {
		commandHandler = sideCarServer;
	}

}
