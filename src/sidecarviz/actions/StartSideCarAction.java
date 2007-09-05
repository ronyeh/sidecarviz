package sidecarviz.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import papertoolkit.util.DebugUtils;
import papertoolkit.util.TestUtil;

/**
 * <p>
 * Init<br>
 * SelectionChanged<br>
 * Run<br>
 * Dispose<br>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StartSideCarAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		DebugUtils.println("Dispose");
	}

	public void init(IWorkbenchWindow window) {
		DebugUtils.println("Init: " + window);
	}

	public void run(IAction action) {
		DebugUtils.println("Run: " + action);

		new TestUtil();

		// openFile();
		// traverseWorkspace();
	}

	private void traverseWorkspace() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IJavaModel javaModel = JavaCore.create(workspace.getRoot());
		try {
			IJavaProject[] javaProjects = javaModel.getJavaProjects();
			for (IJavaProject proj : javaProjects) {
				int numClasses = 0;
				IPackageFragmentRoot[] roots = proj.getAllPackageFragmentRoots();
				for (IPackageFragmentRoot root : roots) {
					IJavaElement[] children = root.getChildren();
					for (IJavaElement elt : children) {
						IPackageFragment frag = (IPackageFragment) elt.getAdapter(IPackageFragment.class);
						if (frag == null) {
							continue;
						}
						// whittle it down to only the classes we care about...
						if (!frag.getElementName().startsWith("papertoolkit")) {
							continue;
						}
						DebugUtils.println(frag.getElementName());
						IJavaElement[] fes = frag.getChildren();
						for (IJavaElement classElt : fes) {
							String className = classElt.getElementName();
							DebugUtils.println(className);
							DebugUtils.println("Path: " + classElt.getPath().toPortableString());
							// DebugUtils.println(classElt.getPath().getClass());
							numClasses++;
						}
					}

				}
				DebugUtils.println("Classpath for Project " + proj.getElementName() + " contains "
						+ numClasses + " classes.");
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void openFile() {

		// get the filepath using a PaperToolkit method... then convert it to an Eclipse Filestore
		String filepath = "C:/Documents and Settings/Ron Yeh/My Documents/Projects-EclipsePlugin"
				+ "/TestApplication/src/edu/stanford/hci/TestApp.java";

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(filepath));
		try {
			IEditorPart editor = IDE.openEditorOnFileStore(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage(), fileStore);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// DebugUtils.println("SelectionChanged: " + action + " / " + selection);

		// if (selection == null) { // never null, apparently
		// return;
		// }

		// always points to this instance...
		// DebugUtils.println(action.getId());

		// figure out where the cursor is
	}

}
