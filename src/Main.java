

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrimArea;

public class Main {

	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		configureShell(shell);
		shell.open();
		while (!shell.isDisposed()) {
			display.readAndDispatch();
		}
	}

	private static void configureShell(Shell shell) {
		shell.setText("Unified Toolbar Test");

		// This next line crashes on macOS Monterrey but works on Big Sur.
		Composite trimArea = TrimArea.createTrimArea(shell);
		if (trimArea != null) {
			Label leftLabel = new Label(trimArea, SWT.NULL);
			leftLabel.setText("Hello");
			leftLabel.setLayoutData(
				new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
	
			Label rightLabel = new Label(trimArea, SWT.NULL);
			rightLabel.setText("World!");
			rightLabel.setLayoutData(
				new GridData(GridData.END, GridData.CENTER, true, false));
			
			trimArea.setLayout(new GridLayout(2, false));
		}
		
		Label content = new Label(shell, SWT.WRAP);
		content.setText("This example app demonstrates putting a Composite "
			+ "into the trim area of the Shell by putting a single ToolItem "
			+ "with SWT.SEPERATOR style into the macOS unified toolbar.\n\n"
			+ "The Composite will cause the trim area to accomodate its "
			+ "layout height, while at the same time following the width of "
			+ "the shell when it is resized.");
		shell.setLayout(new FillLayout());
		shell.setSize(500, 300);
	}
}
