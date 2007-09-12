package sidecarviz;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.LocationHandler;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;

public class PaperApp {
	
	// create some regions
	// bind them at runtime to some place in your notebook
	// save these mappings in the right place
	// test out our event handlers, and monitor them with sidecar
	// event save and replay with sidecar
	public PaperApp() {
		Application application = PaperToolkit.createApplication();
		Sheet genericPage = application.createSheet();
		Region region = genericPage.createRegion(0, 0, 2, 2);
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked at " + e);
			}
		});
		Region inkRegion = genericPage.createRegion(5, 5, 5, 5);
		inkRegion.addEventHandler(new LocationHandler() {
			public void strokeArrived(PenEvent e, InkStroke s) {
				DebugUtils.println("Stroke Arrived");
			}
		});
		application.run();
	}
	public static void main(String[] args) {
		new PaperApp();
	}
}
