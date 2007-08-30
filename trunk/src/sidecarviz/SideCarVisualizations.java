package sidecarviz;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.util.DebugUtils;

public class SideCarVisualizations {

	public SideCarVisualizations() {
		Application app = PaperToolkit.createApplication();
		app.run();
	}

	public static void main(String[] args) {

		new SideCarVisualizations();

	}
}
