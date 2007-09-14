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
	 * 1) Start Firefox: The Firefox Server should start before this.<br>
	 * 2) Start SideCarViz or The Eclipse Plugin: Automatically Launched when instrumented Eclipse starts. This starts before the PaperToolkit.<br>
	 * 3) Start your PaperToolkit App to test... it will add the sidecar option in your system tray.
	 * 4) Finally, the flex application starts, which visualizes everything!<br>
	 * <br>
	 * Firefox actually supports multiple clients... <br>
	 * But, it seems to fail, if SideCar logs off... as the outputstream to one of its clients craps out<br>
	 * However, closing and reopening SideCar's Flash GUI is OK.
	 */
	private SideCarVisualizations() {
		DebugUtils.println("Initializing SideCar...");

		// start a server here... (43210)
		sideCarServer = new SideCarServer(this);

		// The green button opens firefox (which starts its own server at 54321)
		// loads the flex application, which connects to the sidecar server at 43210
		// connect as a client to firefox to receive information
		connectToWebBrowser();
	}

	/**
	 * Press the green button after Firefox is running! We will automatically try to connect to the web
	 * browser on startup... but the green button works too.
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
