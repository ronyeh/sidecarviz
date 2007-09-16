package sidecarviz.core;

import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
import sidecarviz.SideCarVisualizations;

public class SideCarPen {

	private Pen pen;
	private SideCarVisualizations scv;
	private String id;
	private String penIDAttribute;

	public SideCarPen(SideCarVisualizations sideCarVisualizations) {
		scv = sideCarVisualizations;
		pen = new Pen();
		pen.addLivePenListener(getListener());
		id = pen.getID();
		penIDAttribute = " penID=\"" + id + "\" ";
		pen.startLiveMode();
	}

	/**
	 * Can only be called once... Releases resources.
	 */
	public void stop() {
		pen.stopLiveMode();
		pen = null;
	}

	private String getLocationAttribute(PenSample sample) {
		return " x=\"" + sample.x + "\" y=\"" + sample.y + "\" ";
	}

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
}
