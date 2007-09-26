package sidecarviz.editors;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.FileEditorInput;
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
		if (editor instanceof FileEditorInput) {
			textEditor = (ITextEditor) editor;
			DebugUtils.println("Set Active Editor To: " + textEditor.getEditorInput());
			if (textEditor.getEditorInput() instanceof FileEditorInput) {
				FileEditorInput fei = (FileEditorInput) textEditor.getEditorInput();
			}
		}
	}
}
