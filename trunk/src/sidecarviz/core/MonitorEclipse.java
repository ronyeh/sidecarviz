package sidecarviz.core;

import java.util.HashMap;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;

import papertoolkit.util.DebugUtils;
import sidecarviz.SideCarVisualizations;
import sidecarviz.editors.SideCarJavaEditor;

/**
 * <p>
 * All information we get by instrumenting Eclipse is sent through this class, and forwarded to the Flash GUI.
 * If we need to ask Eclipse to do something, we also go through this class.
 * 
 * gotXXXX methods are called by Eclipse, and forwarded to Flash
 * 
 * doXXXX methods are called by anyone, and ask Eclipse to do something
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
@SuppressWarnings("restriction")
public class MonitorEclipse {

	private static MonitorEclipse instance;

	public static MonitorEclipse getInstance() {
		if (instance == null) {
			instance = new MonitorEclipse();
		}
		return instance;
	}

	private HashMap<String, SourceType> javaEditorEditedClasses = new HashMap<String, SourceType>();
	private HashMap<String, SourceMethod> javaEditorEditedMethods = new HashMap<String, SourceMethod>();

	private java.io.File lastFileOpened;
	private SourceMethod lastMethodEdited;

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
			openCompilationUnit(compilationUnit);
		}
	}

	/**
	 * @param cursorPosition
	 */
	public void gotCursorMovementInEditor(String cursorPosition) {
		// DebugUtils.println("Moved To: " + cursorPosition);
	}

	/**
	 * @param sideCarJavaEditor
	 * @param type
	 */
	public void gotEditingClass(SideCarJavaEditor sideCarJavaEditor, SourceType type) {
		// getElementName(); provides the shortname, like PaperUI, or main
		// we want the fully qualified name

		final String className = type.getFullyQualifiedName();
		// DebugUtils.println("Editing Class: " + className);

		// add to hashmap
		javaEditorEditedClasses.put(className, type);
		// TODO: Forward to Flash
	}

	/**
	 * @param sideCarJavaEditor
	 * @param method
	 */
	public void gotEditingMethod(final SideCarJavaEditor sideCarJavaEditor, final SourceMethod method) {
		try {
			// fully qualified has a bug, in that getPackageFragment returns null! :-(
			// use type qualified instead...

			// go up until you find a class....
			// then, prepend the package & class name to the method's name...
			String packageAndClassName = "";
			if (method.getParent().getClass().equals(SourceType.class)) {
				packageAndClassName = ((SourceType) method.getParent()).getFullyQualifiedName();
			}

			final String methodName = method.getTypeQualifiedName('.', true);
			final String fullyQualifiedMethodName = packageAndClassName + "."
					+ methodName.substring(methodName.lastIndexOf(".") + 1);

			// TODO: Forward to Flash if it's not the last method we edited
			if (method != lastMethodEdited) {
				DebugUtils.println("Editing Method: " + fullyQualifiedMethodName);
			}
			lastMethodEdited = method;

			// add to hashmap, for later access
			javaEditorEditedMethods.put(fullyQualifiedMethodName, method);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param file
	 */
	public void gotOpenedFileInEditor(java.io.File file) {
		if (!file.equals(lastFileOpened)) {
			DebugUtils.println("Opened File: " + file);
		}
		lastFileOpened = file;
		SideCarVisualizations.getInstance().changedEditorTo(file);
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
				// DebugUtils.println(o.getClass());
				// unhandled
			}
		}
	}

	/**
	 * TODO: Should Figure Out... Where Did We Copy From?
	 * 
	 * @param copiedText
	 */
	public void gotTextCopiedFromEditor(String copiedText) {
		// replace newlines and stuff...
		String formattedCopiedText = copiedText.replaceAll("\\n", " ").replaceAll("\"", "'").trim();
		DebugUtils.println("Copied " + formattedCopiedText);
		SideCarVisualizations.getInstance().copiedTextFromEditor(formattedCopiedText);
	}

	/**
	 * @param cutText
	 */
	public void gotTextCutFromEditor(String cutText) {
		// replace newlines and stuff...
		String formattedCutText = cutText.replaceAll("\\n", " ").replaceAll("\"", "'").trim();
		DebugUtils.println("Cut: " + formattedCutText);
		SideCarVisualizations.getInstance().cutTextFromEditor(formattedCutText);
	}

	/**
	 * @param pastedText
	 */
	public void gotTextPastedIntoEditor(String pastedText) {
		// replace newlines and stuff...
		String formattedPastedText = pastedText.replaceAll("\\n", " ").replaceAll("\"", "'").trim();
		DebugUtils.println("Pasted: " + formattedPastedText);
		SideCarVisualizations.getInstance().pastedTextIntoEditor(formattedPastedText);
	}

	private void openCompilationUnit(CompilationUnit compilationUnit) {
		try {
			IResource correspondingResource = compilationUnit.getCorrespondingResource();
			if (correspondingResource instanceof org.eclipse.core.internal.resources.File) {
				File eclipseFile = (File) correspondingResource;

				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (workbenchWindow == null) {
					// TODO: DO NOT DO THIS
					// This opens another Eclipse Window...
					// workbenchWindow = PlatformUI.getWorkbench().openWorkbenchWindow(null);
					DebugUtils.println("Workbench Window is Null");
					return;
				}

				IWorkbenchPage activePage = workbenchWindow.getActivePage();
				if (activePage == null) {
					DebugUtils.println("Active Page is Null");
					try {
						activePage = workbenchWindow.openPage(null);
					} catch (WorkbenchException e) {
						e.printStackTrace();
					}
				}
				IDE.openEditor(activePage, eclipseFile);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
