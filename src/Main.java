

import org.eclipse.swt.widgets.Display;
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

		// This next line crashes on macOS Monterrey but used to work on Big Sur.
		TrimArea.createTrimArea(shell);
	}
}
