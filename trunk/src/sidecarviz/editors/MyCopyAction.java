package sidecarviz.editors;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.internal.corext.codemanipulation.ImportReferencesCollector;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.javaeditor.ClipboardOperationAction;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.ui.texteditor.TextEditorAction;

import papertoolkit.util.DebugUtils;

public class MyCopyAction implements IAction {

	private ClipboardOperationAction clip;

	public MyCopyAction(ClipboardOperationAction c) {
		clip = c;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		DebugUtils.println("");
		clip.addPropertyChangeListener(listener);
	}

	public int getAccelerator() {
		DebugUtils.println("");
		return clip.getAccelerator();
	}

	public String getActionDefinitionId() {
		DebugUtils.println("");
		return clip.getActionDefinitionId();
	}

	public String getDescription() {
		DebugUtils.println("");
		return clip.getDescription();
	}

	public ImageDescriptor getDisabledImageDescriptor() {
		DebugUtils.println("");
		return clip.getDisabledImageDescriptor();
	}

	public HelpListener getHelpListener() {
		DebugUtils.println("");
		return clip.getHelpListener();
	}

	public ImageDescriptor getHoverImageDescriptor() {
		DebugUtils.println("");
		return clip.getHoverImageDescriptor();
	}

	public String getId() {
		DebugUtils.println("");
		return clip.getId();
	}

	public ImageDescriptor getImageDescriptor() {
		DebugUtils.println("");
		return clip.getImageDescriptor();
	}

	public IMenuCreator getMenuCreator() {
		DebugUtils.println("");
		return clip.getMenuCreator();
	}

	public int getStyle() {
		DebugUtils.println("");
		return clip.getStyle();
	}

	public String getText() {
		DebugUtils.println("");
		return clip.getText();
	}

	public String getToolTipText() {
		DebugUtils.println("");
		return clip.getToolTipText();
	}

	public boolean isChecked() {
		DebugUtils.println("");
		return clip.isChecked();
	}

	public boolean isEnabled() {
		DebugUtils.println("");
		return clip.isEnabled();
	}

	public boolean isHandled() {
		DebugUtils.println("");
		return clip.isHandled();
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		DebugUtils.println("");
		clip.removePropertyChangeListener(listener);
	}

	public void run() {
		myCopyListener();
		clip.run();
	}

	private void myCopyListener() {
		DebugUtils.println("Copying!");
		java.awt.datatransfer.Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = systemClipboard.getContents(null);
		try {
			String transferData = (String) contents.getTransferData(DataFlavor.stringFlavor);
			DebugUtils.println(transferData);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runWithEvent(Event event) {
		myCopyListener();
		clip.runWithEvent(event);
	}

	public void setAccelerator(int keycode) {
		DebugUtils.println("");
		clip.setAccelerator(keycode);
	}

	public void setActionDefinitionId(String id) {
		DebugUtils.println("");
		clip.setActionDefinitionId(id);
	}

	public void setChecked(boolean checked) {
		DebugUtils.println("");
		clip.setChecked(checked);
	}

	public void setDescription(String text) {
		DebugUtils.println("");
		clip.setDescription(text);
	}

	public void setDisabledImageDescriptor(ImageDescriptor newImage) {
		DebugUtils.println("");
		clip.setDisabledImageDescriptor(newImage);
	}

	public void setEnabled(boolean enabled) {
		DebugUtils.println("");
		clip.setEnabled(enabled);
	}

	public void setHelpListener(HelpListener listener) {
		DebugUtils.println("");
		clip.setHelpListener(listener);
	}

	public void setHoverImageDescriptor(ImageDescriptor newImage) {
		DebugUtils.println("");
		clip.setHoverImageDescriptor(newImage);
	}

	public void setId(String id) {
		DebugUtils.println("");
		clip.setId(id);
	}

	public void setImageDescriptor(ImageDescriptor newImage) {
		DebugUtils.println("");
		clip.setImageDescriptor(newImage);
	}

	public void setMenuCreator(IMenuCreator creator) {
		DebugUtils.println("");
		clip.setMenuCreator(creator);
	}

	public void setText(String text) {
		DebugUtils.println("");
		clip.setText(text);
	}

	public void setToolTipText(String text) {
		DebugUtils.println("");
		clip.setToolTipText(text);
	}
}
