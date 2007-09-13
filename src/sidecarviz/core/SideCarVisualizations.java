package sidecarviz.core;

import papertoolkit.util.DebugUtils;

/**
 * <p>
 * This is the core SideCarVisualizations program. Note: when debugging an Eclipse plugin, you should tail the
 * .log file located at: C:\Documents and Settings\Ron Yeh\My Documents\Projects-EclipsePlugin\.metadata\.log
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SideCarVisualizations {

	private static SideCarVisualizations instance;

	public static synchronized SideCarVisualizations getInstance() {
		if (instance == null) {
			instance = new SideCarVisualizations();
		}
		return instance;
	}

	/**
	 * For testing SideCar separate from the Eclipse Application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new SideCarVisualizations();
	}

	private SideCarClient sideCarClient;
	private SideCarServer sideCarServer;
	private ToolkitListener toolkitListener;

	/**
	 * The Firefox Server can start before this.<br>
	 * The Eclipse Plugin is Launched when Eclipse starts<br>
	 * This starts before the PaperToolkit.<br>
	 * Finally, the flex application starts.<br>
	 */
	private SideCarVisualizations() {
		DebugUtils.println("Initializing SideCar...");

		// start a server here... (43210)
		sideCarServer = new SideCarServer(this);

		// The green button opens firefox (which starts its own server at 54321)

		// loads the flex application, which connects to the sidecar server at 43210

		// connect as a client to firefox to receive information

	}

	/**
	 * Press the green button after Firefox is running!
	 */
	public void connectToWebBrowser() {
		DebugUtils.println("Connect to Web Browser");
		sideCarClient = new SideCarClient("localhost", 54321);
		sideCarClient.setCommandHandler(sideCarServer);
	}

	public void stop() {
		DebugUtils.println("Stopping SideCar...");
		if (sideCarServer != null) {
			sideCarServer.exit();
		}
		if (sideCarClient != null) {
			sideCarClient.exit();
		}
	}

	public void connectToTheToolkit() {
		toolkitListener = new ToolkitListener(this);
	}

	public void sendToFlashGUI(String message) {
		sideCarServer.sendToFlashGUI(message);
	}
}
