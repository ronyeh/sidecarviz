package sidecarviz.editors;

import org.eclipse.jdt.internal.ui.javaeditor.ClipboardOperationAction;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.actions.ActionFactory;

import papertoolkit.util.DebugUtils;
import sidecarviz.handlers.CopyHandler;

public class SideCarJavaEditor extends CompilationUnitEditor {
	private IAction myCopyAction;

	public SideCarJavaEditor() {
		DebugUtils.println("SideCarEditor Initialized");
	}

	public IAction getAction(String actionID) {
		// DebugUtils.println("Looking for Action: " + actionID);
		if (actionID.equals(ActionFactory.COPY.getId())) {
			DebugUtils.println("Returning MyCopyAction");
			return getMyCopyAction(super.getAction(actionID));
		} else {
			return super.getAction(actionID);
		}
	}

	private IAction getMyCopyAction(IAction action) {
		final ClipboardOperationAction c = (ClipboardOperationAction) action;
		if (myCopyAction == null) {
			myCopyAction = new MyCopyAction(c);
			CopyHandler.setCopyAction(myCopyAction);
		}
		return myCopyAction;
	}
}
