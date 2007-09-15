package sidecarviz.core;

import java.io.File;
import java.util.HashMap;

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
	private String lastFileAbsolutePath = "";

	private HashMap<Integer, String> fileIDToPath = new HashMap<Integer, String>();
	private HashMap<String, Integer> pathToFileID = new HashMap<String, Integer>();
	private static int uniqueFileHandle = 0;

	/**
	 * 1) Start Firefox: The Firefox Server should start before this.<br>
	 * 2) Start SideCarViz or The Eclipse Plugin: Automatically Launched when instrumented Eclipse starts.
	 * This starts before the PaperToolkit.<br>
	 * 3) Start your PaperToolkit App to test... it will add the sidecar option in your system tray. 4)
	 * Finally, the flex application starts, which visualizes everything!<br>
	 * <br>
	 * Firefox actually supports multiple clients... <br>
	 * But, it seems to fail, if SideCar logs off... as the outputstream to one of its clients craps out<br>
	 * However, closing and reopening SideCar's Flash GUI is OK.<br>
	 * <br>
	 * TODO: SideCar Flash should be accessible even if the program is not running! =\ Right now, you have to
	 * run your program first. That is, we should switch up #s 3 and 4 in the steps above...
	 */
	private SideCarVisualizations() {
		DebugUtils.println("Initializing SideCar...");

		// start a server here... (43210)
		sideCarServer = new SideCarServer(this);

		// The green button opens firefox (which starts its own server at 54321)
		// loads the flex application, which connects to the sidecar server at 43210
		// connect as a client to firefox to receive information
		connectToWebBrowser();

		// wait a second or two
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// will force firefox to open... and start its server at 54321
		// the flash GUI will connect to us at 43210
		sideCarServer.openFlashGUI();
	}

	/**
	 * Lets us know which Java files we opened. We should store the complete path in a hashmap, and return a
	 * unique file id to the flash GUI. The flash GUI can then use this uid to reference the file.
	 * 
	 * @param javaFile
	 */
	public void changedEditorTo(File javaFile) {
		// only report when this file changes...
		String currAbsolutePath = javaFile.getAbsolutePath();
		if (currAbsolutePath.equals(lastFileAbsolutePath)) {
			return;
		}
		lastFileAbsolutePath = currAbsolutePath;
		// DebugUtils.println("Worked on File: " + javaFile.getAbsoluteFile());

		int fileID = -1;
		// get the file's unique ID
		if (!pathToFileID.containsKey(currAbsolutePath)) {
			fileID = uniqueFileHandle;
			uniqueFileHandle++; // increment for next time
			pathToFileID.put(currAbsolutePath, fileID);
			fileIDToPath.put(fileID, currAbsolutePath);
		} else {
			fileID = pathToFileID.get(currAbsolutePath);
		}
		sendToFlashGUI("<currentlyEditing fileID=\"" + fileID + "\" fileName=\"" + javaFile.getName() + "\"/>");
	}

	/**
	 * 
	 */
	public void connectToTheToolkit() {
		toolkitListener = new ToolkitListener(this);
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

	/**
	 * @param message
	 */
	public void sendToFlashGUI(String message) {
		sideCarServer.sendToFlashGUI(message);
	}

	/**
	 * Kills the instance, so that the next call to getInstance will reinitialize everything!
	 */
	public void stop() {
		DebugUtils.println("Stopping SideCar...");
		if (sideCarServer != null) {
			sideCarServer.exit();
		}
		if (sideCarClient != null) {
			sideCarClient.exit();
		}
		instance = null;
	}
}
