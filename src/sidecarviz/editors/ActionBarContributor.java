package sidecarviz.editors;

import org.eclipse.jdt.internal.ui.javaeditor.ClipboardOperationAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;

import papertoolkit.util.DebugUtils;

public class ActionBarContributor extends TextEditorActionContributor {

	private ITextEditor textEditor;

	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
	}

	public void dispose() {
	}

	public void setActiveEditor(IEditorPart editor) {
		super.setActiveEditor(editor);
		if (editor instanceof ITextEditor)
			textEditor = (ITextEditor) editor;

		// we know this is the class!
		// IAction action = getAction(textEditor, ActionFactory.COPY.getId());
		// DebugUtils.println(action);
		// action.addPropertyChangeListener(new IPropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent event) {
		// // true when something is selected
		// final Object val = event.getNewValue();
		// if (val instanceof Boolean) {
		// final Boolean isTextSelected = (Boolean) val;
		// DebugUtils.println("Text has been " + (isTextSelected ? "selected" : "unselected"));
		// }
		// }
		// });
	}
}
