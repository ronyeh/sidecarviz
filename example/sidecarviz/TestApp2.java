package sidecarviz;

import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.Sheet.SheetSize;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Eventual goal, to develop an application _while_ sidecar is running! =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TestApp2 {

	public TestApp2() {
		Application application = new Application("Test");
		Sheet sheet = new Sheet(SheetSize.B7);
		Region region = sheet.createRegion();
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked at: " + e.getPercentageLocation());
			}
		});

		// track output to devices... new Device(LOCAL) or new Device(REMOTE)... if it's REMOTE (default?)
		// we will ask for the REMOTE computer's IP Address or Host Name at runtime and save it into devices/ or mappings/

		application.addSheet(sheet);
		application.run();
	}

	public static void main(String[] args) {
		new TestApp2();
	}
}
