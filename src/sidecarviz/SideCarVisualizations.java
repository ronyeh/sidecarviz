package sidecarviz;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.thoughtworks.xstream.XStream;

import papertoolkit.PaperToolkit;
import papertoolkit.util.DebugUtils;
import sidecarviz.core.SideCarClient;
import sidecarviz.core.SideCarPen;
import sidecarviz.core.SideCarServer;
import sidecarviz.core.ToolkitListener;

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

	/**
	 * Connects to Firefox over port 54321, and listens for information! The message handlers happen to be
	 * stored in sideCarServer....
	 */
	private SideCarClient sideCarClientForFirefoxBrowser;
	private SideCarServer sideCarServer;
	private ToolkitListener toolkitListener;
	private String lastFileAbsolutePath = "";

	private HashMap<Integer, String> fileIDToPath = new HashMap<Integer, String>();
	private HashMap<String, Integer> pathToFileID = new HashMap<String, Integer>();
	private SideCarPen sideCarPen;
	private static int uniqueFileHandle = 0;

	// because it's a research prototype...
	// this is also done in PaperToolkit, as a fallback for the eclipse plugin... :(
	private static final String SIDECAR_PATH = "C:/Documents and Settings/Ron Yeh/My Documents/Projects/SideCarViz";

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
	 * 
	 * TODO: win32com.dll should be installed in your system path, because eclipse won't be able to find it!
	 * daaargh!
	 */
	private SideCarVisualizations() {
		// DebugUtils.println("Initializing SideCar...");

		// currently, we set PaperToolkit's directory statically
		// TODO: we should use a configuration file or an eclipse preference

		// When running as a plugin, our rundir starts out a C:\Program Files\eclipse\ =\
		// How can we navigate back to the SideCar directory? (and more importantly, the PaperToolkit one?)
		// For now, use constant

		// see if XStream and DebugUtils are accessible here...
		XStream stream = new XStream();
		String xml = stream.toXML(new String("Bob"));
		DebugUtils.println(xml);
		
		
		// start a server here... (43210)
		sideCarServer = new SideCarServer(this);

		// start the local pen...
		sideCarPen = new SideCarPen(this);

		// wait a second or so
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// will force firefox to open... and start its server at 54321
		// the flash GUI will connect to us at 43210
		sideCarServer.openFlashGUI();

		// The green button opens firefox (which starts its own server at 54321)
		// loads the flex application, which connects to the sidecar server at 43210
		// connect as a client to firefox to receive information
		connectToWebBrowser();
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
		sendToFlashGUI("<currentlyEditing fileID=\"" + fileID + "\" fileName=\"" + javaFile.getName()
				+ "\"/>");
	}

	/**
	 * Listens for info from PaperToolkit.
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
		sideCarClientForFirefoxBrowser = new SideCarClient("localhost", 54321);
		sideCarClientForFirefoxBrowser.setCommandHandler(sideCarServer);
	}

	/**
	 * Forward information to Flash!
	 * 
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
			sideCarServer = null;
		}
		if (sideCarClientForFirefoxBrowser != null) {
			sideCarClientForFirefoxBrowser.exit();
			sideCarClientForFirefoxBrowser = null;
		}
		// stop the pen as well... =)
		sideCarPen.stop();
		sideCarPen = null;
		instance = null;
	}
}
