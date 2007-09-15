package sidecarviz.editors;

import java.io.File;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.ClipboardOperationAction;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import papertoolkit.PaperToolkit;
import papertoolkit.util.DebugUtils;
import sidecarviz.actions.SideCarCopyAction;
import sidecarviz.actions.SideCarCutAction;
import sidecarviz.actions.SideCarPasteAction;
import sidecarviz.core.SideCarVisualizations;

public class SideCarJavaEditor extends CompilationUnitEditor {

	private Image tImage;

	public SideCarJavaEditor() {
		// DebugUtils.println("SideCarEditor Initialized");
		IDocumentProvider documentProvider = getDocumentProvider();
		// DebugUtils.println(documentProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#getAction(java.lang.String)
	 */
	public IAction getAction(String actionID) {
		// DebugUtils.println("Looking for Action: " + actionID);
		final IAction storedAction = super.getAction(actionID);
		if (actionID.equals(ActionFactory.COPY.getId())) {
			CompilationUnitDocumentProvider docProvider = (CompilationUnitDocumentProvider) getDocumentProvider();
			return new SideCarCopyAction((ClipboardOperationAction) storedAction);
		} else if (actionID.equals(ActionFactory.CUT.getId())) {
			return SideCarCutAction.getInstance(storedAction);
		} else if (actionID.equals(ActionFactory.PASTE.getId())) {
			return SideCarPasteAction.getInstance(storedAction);
		} else {
			return storedAction;
		}
	}

	protected void initializeEditor() {
		super.initializeEditor();
	}

	public void updatedTitleImage(Image image) {
		if (tImage == null) {
			tImage = new Image(image.getDevice(),
					"C:/Documents and Settings/Ron Yeh/My Documents/Projects/SideCarViz/icons/sun.gif");
		}
		super.updatedTitleImage(tImage);
	}

	protected void selectionChanged() {
		super.selectionChanged();
	}

	public void setSelection(IJavaElement element) {
		super.setSelection(element);
		DebugUtils.println("Selected: " + element.getElementName());

		// TODO: Forward this on to the flash GUI
	}

	protected void disposeDocumentProvider() {
		DebugUtils.println("");
		super.disposeDocumentProvider();
	}

	protected void setDocumentProvider(IEditorInput input) {
		super.setDocumentProvider(input);
		if (input instanceof FileEditorInput) {
			FileEditorInput fei = (FileEditorInput) input;
			String absolutePath = fei.getFile().getLocation().toFile().getAbsolutePath();
			DebugUtils.println("Opened: " + absolutePath);
			// TODO: Forward this on!
		}
	}

	protected void handleEditorInputChanged() {
		super.handleEditorInputChanged();
		DebugUtils.println("");
	}

	protected void handleElementContentReplaced() {
		super.handleElementContentReplaced();
		DebugUtils.println("");
	}

	public void updatePartControl(IEditorInput input) {
		super.updatePartControl(input);
		if (input instanceof FileEditorInput) {
			SideCarVisualizations.getInstance().changedEditorTo(((FileEditorInput)input).getPath().toFile().getAbsoluteFile());
		}
	}

	public INavigationLocation createNavigationLocation() {
		return super.createNavigationLocation();
	}

	protected void setDocumentProvider(IDocumentProvider provider) {
		super.setDocumentProvider(provider);
		DebugUtils.println(provider);
	}
}
