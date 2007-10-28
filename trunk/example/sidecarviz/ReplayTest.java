package sidecarviz;

import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.events.handlers.MarkingGestureHandler;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.Sheet.SheetSize;
import papertoolkit.pen.ink.InkStroke;
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
public class ReplayTest {

	public ReplayTest() {
		Application application = new Application("Replay");
		Sheet sheet = new Sheet(SheetSize.A5);
		Region inkReg = sheet.createRegion(1, 1, 6.5, 4);
		inkReg.addEventHandler(new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				// no println needed!
				// DebugUtils.println("Inked for " + mostRecentStroke.getDuration() + " ms.");
			}
		});

		Region region = sheet.createRegion(1, 6, 3, 2);
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				// no println needed!
				// DebugUtils.println("Clicked at " + e.getPercentageLocation());
			}
		});

		Region markregion = sheet.createRegion(6, 5.5, 2, 2);
		markregion.addEventHandler(new MarkingGestureHandler() {
			public void handleMark(PenEvent e, MarkDirection dir) {
				
			}
		});
		
		application.addSheet(sheet);
		application.run();
		// on run, we assume sidecar is already running
		// start broadcasting information to the toolkit monitor!!!
	}

	public static void main(String[] args) {
		new ReplayTest();
	}
}
