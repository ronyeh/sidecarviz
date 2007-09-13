package sidecarviz.app;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler;
import papertoolkit.events.handlers.HandwritingHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Tests SideCar...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TestApp {

	public TestApp() {
		Application app = PaperToolkit.createApplication();
		Sheet sheet = app.createSheet();
		Region region = sheet.createRegion();
		region.addEventHandler(new ClickHandler() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on Box 1...");
			}

			public void pressed(PenEvent e) {
			}

			public void released(PenEvent e) {
			}
		});

		Region region2 = sheet.createRegion();
		region2.addEventHandler(new ClickHandler() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on Box 2...");
			}

			public void pressed(PenEvent e) {
			}

			public void released(PenEvent e) {
			}
		});
		// region2.addEventHandler(new HandwritingHandler() {
		// public void contentArrived() {
		// String writing = recognizeHandwriting();
		// DebugUtils.println(writing);
		// }
		// });

		app.run();
	}

	public static void main(String[] args) {
		new TestApp();
	}
}
