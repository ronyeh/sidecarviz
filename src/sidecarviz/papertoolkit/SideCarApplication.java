package sidecarviz.papertoolkit;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import papertoolkit.application.Application;
import papertoolkit.util.DebugUtils;

public class SideCarApplication extends Application {

	public SideCarApplication() {
		super("SideCar Application");
	}

	/**
	 * Automatically called by PaperToolkit.
	 * 
	 * @see papertoolkit.application.Application#populateTrayMenu(java.awt.PopupMenu)
	 */
	public void populateTrayMenuExtensions(PopupMenu popupMenu) {
		popupMenu.add("-");
	
		final MenuItem openSideCarItem = new MenuItem("Open SideCar Display");
		openSideCarItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				
				
			}
		});

		popupMenu.add(openSideCarItem);
	}

}
