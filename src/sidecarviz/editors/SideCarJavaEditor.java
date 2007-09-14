package sidecarviz.editors;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.IDocumentProvider;

import papertoolkit.util.DebugUtils;
import sidecarviz.actions.SideCarCopyAction;
import sidecarviz.actions.SideCarCutAction;
import sidecarviz.actions.SideCarPasteAction;

public class SideCarJavaEditor extends CompilationUnitEditor {

	public SideCarJavaEditor() {
		DebugUtils.println("SideCarEditor Initialized");
		IDocumentProvider documentProvider = getDocumentProvider();
		DebugUtils.println(documentProvider);
	}

	public IAction getAction(String actionID) {
		// DebugUtils.println("Looking for Action: " + actionID);
		final IAction storedAction = super.getAction(actionID);
		if (actionID.equals(ActionFactory.COPY.getId())) {
			DebugUtils.println("Returning MyCopyAction");
			return SideCarCopyAction.getInstance(storedAction);
		} else if (actionID.equals(ActionFactory.CUT.getId())) {
			return SideCarCutAction.getInstance(storedAction);
		} else if (actionID.equals(ActionFactory.PASTE.getId())) {
			return SideCarPasteAction.getInstance(storedAction);
		} else {
			return storedAction;
		}
	}
}
