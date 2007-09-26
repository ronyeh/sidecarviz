package sidecarviz.splashHandlers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * <p>
 * This is called as Eclipse starts... It forces access to our plugin, and gives us time to initialize the
 * SideCarVisualizations server....
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @since 3.3
 */
public class EclipseSplashHandler extends AbstractSplashHandler {
	public EclipseSplashHandler() {
		// if necessary, do something while Eclipse is initializing...
	}
	public void init(final Shell splash) {
		// Store the shell
		super.init(splash);
	}
}
