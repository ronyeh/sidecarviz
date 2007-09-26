package sidecarviz.core;

import java.util.HashMap;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import papertoolkit.util.DebugUtils;

/**
 * <p>
 * All information we get by instrumenting Eclipse is sent through this class, and forwarded to the Flash GUI.
 * If we need to ask Eclipse to do something, we also go through this class.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class MonitorEclipse {
	// gotXXXX methods are called by Eclipse, and forwarded to Flash
	// doXXXX methods are called by anyone, and ask Eclipse to do something

	private static MonitorEclipse instance;

	public static MonitorEclipse getInstance() {
		if (instance == null) {
			instance = new MonitorEclipse();
		}
		return instance;
	}
	private HashMap<String, CompilationUnit> packageExplorerSelectedClasses = new HashMap<String, CompilationUnit>();

	private HashMap<String, PackageFragment> packageExplorerSelectedPackages = new HashMap<String, PackageFragment>();

	/**
	 * Opens an Eclipse Editor on a Compilation Unit, if it exists in our HashMap.
	 * 
	 * @param elementName
	 */
	public void doOpenEditorTo(String elementName) {
		if (packageExplorerSelectedClasses.containsKey(elementName)) {
			CompilationUnit compilationUnit = packageExplorerSelectedClasses.get(elementName);
			try {
				IResource correspondingResource = compilationUnit.getCorrespondingResource();
				if (correspondingResource instanceof org.eclipse.core.internal.resources.File) {
					File eclipseFile = (File) correspondingResource;
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
							eclipseFile);
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * When the developer selects an item in the package explorer, we monitor it and save the item in a
	 * hashmap for later access.
	 * 
	 * @param selection
	 */
	public void gotPackageExplorerSelectionChanged(TreeSelection selection) {
		// if we cast it to a string, the selection is between two brackets [....]
		// the stuff after the selection gives us more information
		Object[] selected = selection.toArray();
		for (Object o : selected) {
			Class<?> selectedItemType = o.getClass();
			if (selectedItemType.equals(PackageFragment.class)) {
				PackageFragment frag = (PackageFragment) o;
				String fragName = frag.getElementName();

				DebugUtils.println("Package: " + fragName);
				// store the element in a hashmap, for later access
				packageExplorerSelectedPackages.put(fragName, frag);

				// TODO: forward to Flash
			} else if (selectedItemType.equals(CompilationUnit.class)) {
				CompilationUnit comp = (CompilationUnit) o;
				String compName = comp.getElementName();
				IPath compPath = comp.getPath();

				DebugUtils.println("Class: " + compName + "   " + compPath);
				// store the element in a hashmap, for later access
				packageExplorerSelectedClasses.put(compName, comp);

				// TODO: forward to Flash
			} else {
				DebugUtils.println(o.getClass());
				// unhandled
			}
		}
	}

	/**
	 * TODO: Should Figure Out... Where Did We Copy From?
	 * @param copiedText
	 */
	public void gotTextCopiedFromEditor(String copiedText) {
		DebugUtils.println("Copied " + copiedText);
	}

	public void gotTextCutFromEditor(String cutText) {
		DebugUtils.println("Cut: " + cutText);
	}

	public void gotTextPastedIntoEditor(String pastedText) {
		DebugUtils.println("Pasted: " + pastedText);
	}

}
