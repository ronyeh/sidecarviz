package sidecarviz.core;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import papertoolkit.flash.FlashCommand;
import papertoolkit.flash.FlashCommunicationServer;
import papertoolkit.util.DebugUtils;

public class SideCarServer {
	private FlashCommunicationServer server;

	public SideCarServer() {
		// [[commandName]]{{arg1}}{{arg2}}{{arg3}}
		DebugUtils.println("Starting External Communications Server");
		server = new FlashCommunicationServer(43210);
		addSupportedCommands();
		DebugUtils.println("Server Started");
	}

	private void addSupportedCommands() {
		// takes one argument, the URL
		server.addCommand("SC::ClipboardContentsChanged", new FlashCommand() {
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
		server.addCommand("SC::NewPage", new FlashCommand() {
			public void invoke(String... args) {
				DebugUtils.println("Browsed to URL: " + args[0]);
			}
		});
		server.addCommand("SC::NewTab", new FlashCommand() {
			public void invoke(String... args) {
				DebugUtils.println("New Firefox Tab");
			}
		});
		server.addCommand("SC::UserTyped", new FlashCommand() {
			public void invoke(String... args) {
				DebugUtils.println("User Typed: " + args[0]);
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
