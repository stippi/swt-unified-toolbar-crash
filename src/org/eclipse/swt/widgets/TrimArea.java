package org.eclipse.swt.widgets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;

/**
 * The purpose of TrimArea is to embed a Composite spanning the width of the
 * Shell in its trim area by using a single ToolItem.
 * 
 * A ControlListener is added to the Shell in order to resize the trim area
 * together with the Shell.
 * 
 * This class is located in the org.eclipse.swt.widgets package because we need
 * to override Composite.getMinimumHeight() which is package-private.
 *
 * @author stippi
 *
 */
public class TrimArea {
	
	public static Composite createTrimArea(final Shell shell) {
		Point shellSize = shell.getSize();
		
		final ToolBar toolBar = shell.getToolBar();
		if (toolBar == null)
			return null;

		final ToolItem tbItem = new ToolItem(toolBar, SWT.SEPARATOR);
		final Composite area = new Composite(toolBar, SWT.NO_BACKGROUND) {
			@SuppressWarnings("unused")
			@Override
			int getMininumHeight () {
				// ToolItem uses this method to figure out the height
				// in computeSize() and SWT.SEPARATOR style.
				// The Control default implementation returns 0.
				return computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			}
		};

		tbItem.setControl(area);
		// Avoid getting a plain separator item
		tbItem.setWidth(1);

		// Restore shell size to what it was before adding the tool bar
		shell.setSize(shellSize);
		
		shell.addControlListener(new ToolAreaResizer(shell, toolBar, tbItem,
			area));
		
		return area;
	}
	
	private static class ToolAreaResizer extends ControlAdapter
		implements Runnable {
		
		private final Shell		fShell;
		private final ToolBar	fToolBar;
		private final ToolItem	fItem;
		private final Composite	fArea;

		private boolean			fLayoutScheduled;
		
		ToolAreaResizer(Shell shell, ToolBar toolBar, ToolItem item,
			Composite area) {
			fShell = shell;
			fToolBar = toolBar;
			fItem = item;
			fArea = area;
			scheduleLayout();
		}
		
		@Override
		public void controlResized(ControlEvent e) {
			scheduleLayout();
		}
		
		private void scheduleLayout() {
			if (fLayoutScheduled || fShell.isDisposed()) {
				return;
			}
			fLayoutScheduled = true;
			fShell.getDisplay().timerExec(50, this);
			
		}
		
		@Override
		public void run() {
			resize(fShell, fToolBar, fItem, fArea);
			fLayoutScheduled = false;
		}
		
		private void resize(Shell shell, ToolBar toolBar, ToolItem tbItem,
			Composite area) {
			final Point size = shell.getSize();
			int width = size.x - 9;
			
			int maxHeight = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			tbItem.setWidth(width);
			Point areaSize = area.computeSize(width, SWT.DEFAULT);
	
			try {
				Class<?> nsSizeClass = Class.forName(
					"org.eclipse.swt.internal.cocoa.NSSize");
				Class<?> nsToolBarItemClass = Class.forName(
					"org.eclipse.swt.internal.cocoa.NSToolbarItem");
				
				Constructor<?> constructor = nsSizeClass.getConstructor();
				Object newSize = constructor.newInstance();
				
				Field nsItemField = ToolItem.class
					.getDeclaredField("nsItem");
				Object nsItem = nsItemField.get(tbItem);
				
				nsSizeClass.getDeclaredField("width")
					.setFloat(newSize, areaSize.x);
				nsSizeClass.getDeclaredField("height")
					.setFloat(newSize, Math.max(areaSize.y, maxHeight));
	
				Method setMaxSizeMethod = nsToolBarItemClass
					.getDeclaredMethod("setMaxSize", nsSizeClass);
				Method setMinSizeMethod = nsToolBarItemClass
					.getDeclaredMethod("setMinSize", nsSizeClass);
				
				setMaxSizeMethod.invoke(nsItem, newSize);
				setMinSizeMethod.invoke(nsItem, newSize);
			} catch (Exception e) {
				System.out.println("Failed to process resize event: " + e);
			}
	
			area.setSize(areaSize.x, areaSize.y);
			
			// Modifying the ToolItem size may resize the shell, but we
			// don't want this, so restore the original size from the event
			if (!size.equals(shell.getSize()))
				shell.setSize(size.x, size.y);
		}
	}
}
