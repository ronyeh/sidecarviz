package sidecarviz.core;

import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
import sidecarviz.SideCarVisualizations;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SideCarPen {

	private String id;
	private Pen pen;
	private String penIDAttribute;
	private SideCarVisualizations scv;

	/**
	 * @param sideCarVisualizations
	 */
	public SideCarPen(SideCarVisualizations sideCarVisualizations) {
		scv = sideCarVisualizations;
		pen = new Pen();
		pen.addLivePenListener(getListener());
		id = pen.getID();
		penIDAttribute = " penID=\"" + id + "\" ";
		pen.startLiveMode();
	}

	/**
	 * @return
	 */
	private PenListener getListener() {
		return new PenListener() {
			public void penDown(PenSample sample) {
				// DebugUtils.println("SideCar Pen Down");
				scv.sendToFlashGUI("<penDown" + penIDAttribute + getLocationAttribute(sample) + "/>");
			}

			public void penUp(PenSample sample) {
				// DebugUtils.println("SideCar Pen Up");
				scv.sendToFlashGUI("<penUp" + penIDAttribute + getLocationAttribute(sample) + "/>");
			}

			public void sample(PenSample sample) {
				scv.sendToFlashGUI("<penSample" + penIDAttribute + getLocationAttribute(sample) + "/>");
			}
		};
	}

	/**
	 * @param sample
	 * @return
	 */
	private String getLocationAttribute(PenSample sample) {
		return " x=\"" + sample.x + "\" y=\"" + sample.y + "\" ";
	}

	/**
	 * Can only be called once... Releases resources.
	 */
	public void stop() {
		pen.stopLiveMode();
		pen = null;
	}
}
