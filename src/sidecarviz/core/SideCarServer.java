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
import papertoolkit.util.DebugUtils;

public class SideCarServer {
	private ExternalCommunicationServer server;
	private SideCarVisualizations viz;

	public SideCarServer(SideCarVisualizations scv) {
		viz = scv;
		// [[commandName]]{{arg1}}{{arg2}}{{arg3}}
		// DebugUtils.println("Starting External Communications Server");
		server = new ExternalCommunicationServer(Ports.SIDE_CAR_COMMUNICATIONS);
		addSupportedCommands();
		// DebugUtils.println("Server Started");
	}

	private void addSupportedCommands() {
		// takes one argument, the URL
		server.addCommand("SC::ClipboardContentsChanged", new ExternalCommand() {
			// url
			// clipboard contents
			public void invoke(String... args) {
				DebugUtils.println("Clipboard Changed at URL: " + args[0]);
				try {
					String clipboardData = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(
							DataFlavor.stringFlavor);
					// DebugUtils.println(clipboardData);
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
		server.addCommand("SC::NewPage", new ExternalCommand() {
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
		server.addCommand("SC::NewTab", new ExternalCommand() {
			public void invoke(String... args) {
				DebugUtils.println("New Firefox Tab");
			}
		});
		server.addCommand("SC::UserTyped", new ExternalCommand() {
			public void invoke(String... args) {
				DebugUtils.println("User Typed: " + args[0]);
			}
		});
		server.addCommand("StartFlexGUI", new ExternalCommand() {
			public void invoke(String... args) {
				DebugUtils.println("Starting the SideCar Flex GUI");

				// connect to the toolkit monitoring service...
				viz.connectToTheToolkit();

				// open the sidecar flex gui...
				openFlashGUI();
			}
		});
	}

	public void exit() {
		server.exitServer();
	}

	public void handleCommand(String line) {
		server.handleCommand(line);
	}

	/**
	 * Well, since we are running it from the Eclipse plugin... we should hard-code the path for now.
	 */
	public void openFlashGUI() {
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

}
