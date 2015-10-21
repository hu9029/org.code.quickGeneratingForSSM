package org.code.quickGenerating.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class PbDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected ProgressBar progressBar;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PbDialog(Shell parent) {
		super(parent, parent.getStyle());
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	public void close(){
		progressBar.dispose();
		shell.close();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(445, 135);
		shell.setText("\u8BF7\u7B49\u5F85");
		
		progressBar = new ProgressBar(shell, SWT.HORIZONTAL|SWT.SMOOTH|SWT.INDETERMINATE);
		progressBar.setBounds(40, 29, 364, 32);

	}
}
