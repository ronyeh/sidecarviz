package sidecarviz.core;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import papertoolkit.application.config.Constants.Ports;
import papertoolkit.external.ExternalCommand;
import papertoolkit.external.ExternalCommunicationServer;
import papertoolkit.tools.monitor.ToolkitMonitoringService;
import papertoolkit.util.DebugUtils;
import sidecarviz.SideCarVisualizations;

/**
 * <p>
 * Listens to the instrumented Eclipse and Firefox, and forwards information onto the Flash GUI.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SideCarServer {

	private ExternalCommunicationServer server;
	private SideCarVisualizations viz;

	/**
	 * @param scv
	 */
	public SideCarServer(SideCarVisualizations scv) {
		viz = scv;
		server = new ExternalCommunicationServer(Ports.SIDE_CAR_COMMUNICATIONS);
		server.setMaxNumClients(1);
		server.setVerbose(true);
		addSupportedCommands();
	}

	/**
	 * In actuality, we only ever have one client (the Flash GUI that we send data to). However, we populate
	 * the server with a bunch of commands for handling integration with Firefox and the toolkit, since it can
	 * parse out the command and arguments...
	 * 
	 * Commands should look like %%commandName%%@_arg1_@@_arg2_@@_arg3_@
	 * 
	 * We picked these symbols because they are usually not used in regexp, etc.
	 */
	private void addSupportedCommands() {

		// FIREFOX INTEGRATION
		// Commands that start with SC:: come from Firefox

		// takes one argument, the URL
		server.addCommand("Firefox::ClipboardContentsChanged", new ExternalCommand() {
			// url
			// clipboard contents
			public void invoke(String... args) {
				// DebugUtils.println("Clipboard Changed at URL: " + args[0]);
				try {
					String clipboardData = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(
							DataFlavor.stringFlavor);
					DebugUtils.println("Firefox Copy... Clipboard Changed: " + clipboardData);
					// track all copies
					sendToFlashGUI("<clipboardChanged url=\"" + args[0] + "\" contents=\"" + clipboardData
							+ "\"/>");

				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		server.addCommand("Firefox::NewPage", new ExternalCommand() {
			public void invoke(String... args) {
				String url = args[0];
				// track all search queries!
				// special case the google search
				// if it contains www.google.com/search
				// take everything between the q= and the next & (or the end of the string)
				// replace +'s with spaces
				// that is the search query
				if (url.contains("www.google.com/search?")) {
					String searchTerm = getSearchTerm(url);
					sendToFlashGUI("<googleSearch searchQuery=\"" + searchTerm + "\"/>");
					DebugUtils.println("Google Searched For: " + searchTerm);
				} else if (url.contains("www.google.com/codesearch?")) {
					String searchTerm = getSearchTerm(url);
					sendToFlashGUI("<googleCodeSearch searchQuery=\"" + searchTerm + "\"/>");
				} else {
					// track all page changes...
					// Browsed To
					DebugUtils.println("Browsed to URL: " + args[0]);
					sendToFlashGUI("<browsedTo url=\"" + args[0] + "\"/>");
				}

				// Q: can we detect clicks?
			}

		});
		server.addCommand("Firefox::NewTab", new ExternalCommand() {
			public void invoke(String... args) {
				// Not very useful for us to monitor at this point...
				// NewPage will eventually be called...
				// DebugUtils.println("New Firefox Tab");
			}
		});
		server.addCommand("Firefox::UserTyped", new ExternalCommand() {
			public void invoke(String... args) {
				DebugUtils.println("User Typed: " + args[0]);
			}
		});

		
		// PAPER TOOLKIT INTEGRATION
		// the Paper App should send us this command, so that we connect to the monitor
		server.addCommand(ToolkitMonitoringService.START_SIDECAR, new ExternalCommand() {
			public void invoke(String... args) {
				// connect to the toolkit monitoring service...
				viz.connectToTheToolkit();

				// can we check if the flash GUI is still around???
				// apparently not! :-(

				if (!SideCarVisualizations.OPEN_FLASH_GUI_ON_START) {
					// open the sidecar flex gui...NOW! =)
					openFlashGUI();
				}
			}
		});

		// manually reopen the flash GUI
		server.addCommand(ToolkitMonitoringService.START_SIDECAR_GUI, new ExternalCommand() {
			public void invoke(String... args) {
				// open the sidecar flex gui...NOW! =)
				openFlashGUI();
			}
		});
		
		
		
		// FLASH GUI INTEGRATION
		// 
		server.addCommand(new ExternalCommand("Flex::ReplayFromBeginning") {
			public void invoke(String... args) {
				DebugUtils.println("Received " + getName());
			}
		});
		server.addCommand(new ExternalCommand("Flex::OpenUIComponent") {
			public void invoke(String... args) {
				DebugUtils.println("Received " + getName());
			}
		});

	}

	public void exit() {
		server.exitServer();
	}

	/**
	 * @param url
	 * @return
	 */
	private String getSearchTerm(String url) {
		String searchTerm = "";
		// Searched For
		int indexOfQuestionMark = url.indexOf("?");
		// get the parameters, after the ?
		String params = url.substring(indexOfQuestionMark + 1);
		String[] paramArr = params.split("&");
		for (String param : paramArr) {
			if (param.startsWith("q=")) {
				searchTerm = param.substring(2);
			}
		}
		searchTerm = searchTerm.replaceAll("\\+", " ");
		return searchTerm;
	}

	/**
	 * @param line
	 */
	public void handleCommand(String line) {
		server.handleCommand(line);
	}

	/**
	 * Well, since we are running it from the Eclipse plugin... we should hard-code the path for now.
	 */
	public void openFlashGUI() {
		DebugUtils.println("Starting the SideCar Flex GUI");
		final File sidecarHTML = new File(
				"C:/Documents and Settings/Ron Yeh/My Documents/Projects/SideCarViz/flash/bin/SideCar.html");
		server.openFlashHTMLGUI(sidecarHTML);
	}

	/**
	 * Sends information to the Flash GUI...
	 * 
	 * @param message
	 */
	public void sendToFlashGUI(String message) {
		server.sendMessage(message);
	}

}
