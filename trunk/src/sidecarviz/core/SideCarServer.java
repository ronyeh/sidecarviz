package sidecarviz.core;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import papertoolkit.application.config.Constants.Ports;
import papertoolkit.external.ExternalCommand;
import papertoolkit.external.ExternalCommunicationServer;
import papertoolkit.util.DebugUtils;

public class SideCarServer {
	private ExternalCommunicationServer server;


	public SideCarServer() {
		// [[commandName]]{{arg1}}{{arg2}}{{arg3}}
		DebugUtils.println("Starting External Communications Server");
		server = new ExternalCommunicationServer(Ports.SIDE_CAR_COMMUNICATIONS);
		addSupportedCommands();
		DebugUtils.println("Server Started");
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
					DebugUtils.println(clipboardData);
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
				DebugUtils.println("Browsed to URL: " + args[0]);
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
				
				// open the sidecar flex gui...
				
				
			}
		});
	}

	public void exit() {
		server.exitServer();
	}

	public void handleCommand(String line) {
		server.handleCommand(line);
	}
}
