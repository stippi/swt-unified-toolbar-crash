# SWT unified-toolbar crash

Reproducable test case for https://bugs.eclipse.org/bugs/show_bug.cgi?id=568881

Demonstrates using a "TrimArea" in the unified toolbar of macOS app windows.

## Steps to reproduce

1. Import into Eclipse workspace
2. SWT project must be in the workspace as `org.eclipse.swt`.
   The most recent version at the time of writing is 4.21 and produces the crash.
3. Launch the Main class as Java Application.
