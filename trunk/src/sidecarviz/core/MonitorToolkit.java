package sidecarviz.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import papertoolkit.application.config.Constants.Ports;
import papertoolkit.util.DebugUtils;
import sidecarviz.SideCarVisualizations;

/**
 * <p>
 * Receives information broadcast from PaperToolkit, and forwards it on to the Flash GUI...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class MonitorToolkit {

	private Socket toolkitConnection;
	private SideCarVisualizations viz;

	/**
	 * @param sideCarVisualizations
	 */
	public MonitorToolkit(final SideCarVisualizations sideCarVisualizations) {
		viz = sideCarVisualizations;
		try {
			toolkitConnection = new Socket("localhost", Ports.TOOLKIT_MONITORING);
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(toolkitConnection
					.getInputStream()));
			new Thread(new Runnable() {
				private boolean done = false;

				public void run() {
					while (true) {
						if (done) {
							return;
						}
						try {
							String inputLine = bufferedReader.readLine();
							// forward it on to the flex client
							viz.sendToFlashGUI(inputLine);
						} catch (IOException e) {
							done = true;
							DebugUtils.println("It's likely that PaperToolkit has exited...");
							try {
								toolkitConnection.close();
							} catch (IOException ioe) {
								// ioe.printStackTrace();
							}
							// e.printStackTrace();
						}
					}
				}
			}).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
