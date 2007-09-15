package sidecarviz.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.eclipse.jdt.internal.ui.javaeditor.ClipboardOperationAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;

import papertoolkit.util.DebugUtils;
import sidecarviz.handlers.CopyHandler;

public class SideCarCopyAction implements IAction {

	private ClipboardOperationAction clip;

	public SideCarCopyAction(ClipboardOperationAction c) {
		DebugUtils.println("Created a Copy Action");
		clip = c;
		CopyHandler.setCopyAction(this);
	}

	/**
	 * 
	 */
	private void actionPerformed() {
		java.awt.datatransfer.Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = systemClipboard.getContents(null);
		try {
			String transferData = (String) contents.getTransferData(DataFlavor.stringFlavor);
			DebugUtils.println("Copied: ");
			System.out.println(transferData);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// //////////////// Don't Worry about the Methods after Here.... They're just delegates.

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		// DebugUtils.println(""); // this is called

		clip.addPropertyChangeListener(listener);
	}

	public int getAccelerator() {
		// DebugUtils.println("");
		return clip.getAccelerator();
	}

	public String getActionDefinitionId() {
		// DebugUtils.println("");
		return clip.getActionDefinitionId();
	}

	public String getDescription() {
		// DebugUtils.println("");
		return clip.getDescription();
	}

	public ImageDescriptor getDisabledImageDescriptor() {
		// DebugUtils.println("");
		return clip.getDisabledImageDescriptor();
	}

	public HelpListener getHelpListener() {
		DebugUtils.println("");
		return clip.getHelpListener();
	}

	public ImageDescriptor getHoverImageDescriptor() {
		// DebugUtils.println("");
		return clip.getHoverImageDescriptor();
	}

	public String getId() {
		// DebugUtils.println("");
		return clip.getId();
	}

	public ImageDescriptor getImageDescriptor() {
		// DebugUtils.println("");
		return clip.getImageDescriptor();
	}

	public IMenuCreator getMenuCreator() {
		// DebugUtils.println("");
		return clip.getMenuCreator();
	}

	public int getStyle() {
		// DebugUtils.println("");
		return clip.getStyle();
	}

	public String getText() {
		// DebugUtils.println("");
		return clip.getText();
	}

	public String getToolTipText() {
		DebugUtils.println("");
		return clip.getToolTipText();
	}

	public boolean isChecked() {
		// DebugUtils.println("");
		return clip.isChecked();
	}

	public boolean isEnabled() {
		// DebugUtils.println(""); // This is called
		return clip.isEnabled();
	}

	public boolean isHandled() {
		// DebugUtils.println("");
		return clip.isHandled();
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		// DebugUtils.println(""); // This is called
		clip.removePropertyChangeListener(listener);
	}

	public void run() {
		clip.run();
		actionPerformed();
	}

	public void runWithEvent(Event event) {
		clip.runWithEvent(event);
		actionPerformed();
	}

	public void setAccelerator(int keycode) {
		// DebugUtils.println("");
		clip.setAccelerator(keycode);
	}

	public void setActionDefinitionId(String id) {
		// DebugUtils.println("");
		clip.setActionDefinitionId(id);
	}

	public void setChecked(boolean checked) {
		// DebugUtils.println("");
		clip.setChecked(checked);
	}

	public void setDescription(String text) {
		// DebugUtils.println("");
		clip.setDescription(text);
	}

	public void setDisabledImageDescriptor(ImageDescriptor newImage) {
		// DebugUtils.println("");
		clip.setDisabledImageDescriptor(newImage);
	}

	public void setEnabled(boolean enabled) {
		// DebugUtils.println("");
		clip.setEnabled(enabled);
	}

	public void setHelpListener(HelpListener listener) {
		// DebugUtils.println("");
		clip.setHelpListener(listener);
	}

	public void setHoverImageDescriptor(ImageDescriptor newImage) {
		// DebugUtils.println("");
		clip.setHoverImageDescriptor(newImage);
	}

	public void setId(String id) {
		// DebugUtils.println("");
		clip.setId(id);
	}

	public void setImageDescriptor(ImageDescriptor newImage) {
		// DebugUtils.println("");
		clip.setImageDescriptor(newImage);
	}

	public void setMenuCreator(IMenuCreator creator) {
		// DebugUtils.println("");
		clip.setMenuCreator(creator);
	}

	public void setText(String text) {
		// DebugUtils.println("");
		clip.setText(text);
	}

	public void setToolTipText(String text) {
		// DebugUtils.println("");
		clip.setToolTipText(text);
	}
}
