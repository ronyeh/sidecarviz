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
import sidecarviz.core.MonitorEclipse;
import sidecarviz.handlers.CutHandler;

public class SideCarCutAction implements IAction {

	private static IAction instance;

	public static IAction getInstance(IAction action) {
		if (instance == null) {
			instance = new SideCarCutAction((ClipboardOperationAction) action);
			CutHandler.setCutAction(instance);
		}
		return instance;
	}

	/**
	 * The actual action we are intercepting.
	 */
	private ClipboardOperationAction clip;

	private SideCarCutAction(ClipboardOperationAction c) {
		clip = c;
	}

	/**
	 * Developer Cut some text...
	 */
	private void actionPerformed() {
		java.awt.datatransfer.Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = systemClipboard.getContents(null);
		try {
			String cutText = (String) contents.getTransferData(DataFlavor.stringFlavor);
			MonitorEclipse.getInstance().gotTextCutFromEditor(cutText);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// //////////////// Don't Worry about the Methods after Here.... They're just delegates.

	public void addPropertyChangeListener(IPropertyChangeListener listener) {

		clip.addPropertyChangeListener(listener);
	}

	public int getAccelerator() {
		return clip.getAccelerator();
	}

	public String getActionDefinitionId() {
		return clip.getActionDefinitionId();
	}

	public String getDescription() {
		return clip.getDescription();
	}

	public ImageDescriptor getDisabledImageDescriptor() {
		return clip.getDisabledImageDescriptor();
	}

	public HelpListener getHelpListener() {
		return clip.getHelpListener();
	}

	public ImageDescriptor getHoverImageDescriptor() {
		return clip.getHoverImageDescriptor();
	}

	public String getId() {
		return clip.getId();
	}

	public ImageDescriptor getImageDescriptor() {
		return clip.getImageDescriptor();
	}

	public IMenuCreator getMenuCreator() {
		return clip.getMenuCreator();
	}

	public int getStyle() {
		return clip.getStyle();
	}

	public String getText() {
		return clip.getText();
	}

	public String getToolTipText() {
		return clip.getToolTipText();
	}

	public boolean isChecked() {
		return clip.isChecked();
	}

	public boolean isEnabled() {
		return clip.isEnabled();
	}

	public boolean isHandled() {
		return clip.isHandled();
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
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
		clip.setAccelerator(keycode);
	}

	public void setActionDefinitionId(String id) {
		clip.setActionDefinitionId(id);
	}

	public void setChecked(boolean checked) {
		clip.setChecked(checked);
	}

	public void setDescription(String text) {
		clip.setDescription(text);
	}

	public void setDisabledImageDescriptor(ImageDescriptor newImage) {
		clip.setDisabledImageDescriptor(newImage);
	}

	public void setEnabled(boolean enabled) {
		clip.setEnabled(enabled);
	}

	public void setHelpListener(HelpListener listener) {
		clip.setHelpListener(listener);
	}

	public void setHoverImageDescriptor(ImageDescriptor newImage) {
		clip.setHoverImageDescriptor(newImage);
	}

	public void setId(String id) {
		clip.setId(id);
	}

	public void setImageDescriptor(ImageDescriptor newImage) {
		clip.setImageDescriptor(newImage);
	}

	public void setMenuCreator(IMenuCreator creator) {
		clip.setMenuCreator(creator);
	}

	public void setText(String text) {
		clip.setText(text);
	}

	public void setToolTipText(String text) {
		clip.setToolTipText(text);
	}
}
