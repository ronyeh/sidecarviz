package sidecarviz.editors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.internal.ui.javaeditor.ClipboardOperationAction;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import papertoolkit.util.DebugUtils;
import sidecarviz.SideCarVisualizations;
import sidecarviz.actions.SideCarCopyAction;
import sidecarviz.actions.SideCarCutAction;
import sidecarviz.actions.SideCarPasteAction;

/**
 * <p>
 * Customizes the Eclipse Java Editor...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SideCarJavaEditor extends CompilationUnitEditor {

	private Image tImage;

	/**
	 * 
	 */
	public SideCarJavaEditor() {
		// DebugUtils.println("SideCarEditor Initialized");
		IDocumentProvider documentProvider = getDocumentProvider();
		// DebugUtils.println(documentProvider);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#disposeDocumentProvider()
	 */
	protected void disposeDocumentProvider() {
		DebugUtils.println("");
		super.disposeDocumentProvider();
	}

	/**
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

	/**
	 * Fired when the cursor moves... The string is of the form LineNum : CharNum, where each starts from 1
	 * (not 0)...
	 * 
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#handleCursorPositionChanged()
	 */
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		// DebugUtils.println("Cursor Changed: " + getCursorPosition());
		// This can be used to review/replay the interaction somehow??
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.StatusTextEditor#handleEditorInputChanged()
	 */
	protected void handleEditorInputChanged() {
		super.handleEditorInputChanged();
		DebugUtils.println("");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.StatusTextEditor#handleElementContentReplaced()
	 */
	protected void handleElementContentReplaced() {
		super.handleElementContentReplaced();
		DebugUtils.println("");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#initializeEditor()
	 */
	protected void initializeEditor() {
		super.initializeEditor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#selectionChanged()
	 */
	protected void selectionChanged() {
		super.selectionChanged();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setDocumentProvider(org.eclipse.ui.texteditor.IDocumentProvider)
	 */
	protected void setDocumentProvider(IDocumentProvider provider) {
		super.setDocumentProvider(provider);
		DebugUtils.println("Document Provider Set to: " + provider);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#setDocumentProvider(org.eclipse.ui.IEditorInput)
	 */
	protected void setDocumentProvider(IEditorInput input) {
		super.setDocumentProvider(input);
		if (input instanceof FileEditorInput) {
			FileEditorInput fei = (FileEditorInput) input;
			String absolutePath = fei.getFile().getLocation().toFile().getAbsolutePath();
			DebugUtils.println("Opened: " + absolutePath);
			// TODO: Forward this on!
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setFocus()
	 */
	public void setFocus() {
		super.setFocus();
		DebugUtils.println("Focus");
	}

	/**
	 * This tracks what item was selected in the package explorer. It can be a compilation unit (*.java file)
	 * or something finer grained, like a method or class.
	 * 
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#setSelection(org.eclipse.jdt.core.IJavaElement)
	 */
	public void setSelection(IJavaElement element) {
		super.setSelection(element);
		DebugUtils.println("Selected: " + element.getElementName());

		// TODO: Forward this on to the flash GUI
	}

	/**
	 * This is fired when the person selects a method...or class.
	 * 
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#setSelection(org.eclipse.jdt.core.ISourceReference,
	 *      boolean)
	 */
	protected void setSelection(ISourceReference reference, boolean moveCursor) {
		super.setSelection(reference, moveCursor);
		if (reference == null) {
			// selected nothing
			return;
		}
		DebugUtils.println("SetSelection to: " + reference);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#updatedTitleImage(org.eclipse.swt.graphics.Image)
	 */
	public void updatedTitleImage(Image image) {
		if (tImage == null) {
			tImage = new Image(image.getDevice(),
					"C:/Documents and Settings/Ron Yeh/My Documents/Projects/SideCarViz/icons/sun.gif");
		}
		super.updatedTitleImage(tImage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.StatusTextEditor#updatePartControl(org.eclipse.ui.IEditorInput)
	 */
	public void updatePartControl(IEditorInput input) {
		super.updatePartControl(input);
		if (input instanceof FileEditorInput) {
			SideCarVisualizations.getInstance().changedEditorTo(
					((FileEditorInput) input).getPath().toFile().getAbsoluteFile());
		}
	}
}
