
package sidecarviz.splashHandlers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

import papertoolkit.util.DebugUtils;

/**
 * @since 3.3
 * 
 */
public class EclipseSplashHandler extends AbstractSplashHandler {

	/**
	 * 
	 */
	public EclipseSplashHandler() {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.ui.IWorkbench)
	 */
	public void init(final Shell splash) {
		// Store the shell
		super.init(splash);

		
		// Initialize SideCar Here...
		DebugUtils.println("Initializing SideCar...");
	}
}
