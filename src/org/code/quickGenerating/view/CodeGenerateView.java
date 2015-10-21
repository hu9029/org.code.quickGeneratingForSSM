package org.code.quickGenerating.view;

import org.code.quickGenerating.CodeGenerating;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CodeGenerateView extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;
	private IJavaProject project;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CodeGenerateView(IJavaProject project,Shell parent, int style) {
		super(parent, style);
		this.project = project;
		setText("��������");
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

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(533, 176);
		shell.setText("\u4EE3\u7801\u751F\u6210");
		shell.setLayout(new org.eclipse.swt.layout.FormLayout());
		
		text = new Text(shell, SWT.BORDER);
		org.eclipse.swt.layout.FormData fd_text = new org.eclipse.swt.layout.FormData();
		fd_text.right = new FormAttachment(100, -23);
		fd_text.top = new FormAttachment(0, 10);
		text.setLayoutData(fd_text);
		
		text_1 = new Text(shell, SWT.BORDER);
		org.eclipse.swt.layout.FormData fd_text_1 = new org.eclipse.swt.layout.FormData();
		fd_text_1.left = new FormAttachment(text, 0, SWT.LEFT);
		fd_text_1.top = new FormAttachment(text, 13);
		fd_text_1.right = new FormAttachment(100, -23);
		text_1.setLayoutData(fd_text_1);
		
		Label lblDao = new Label(shell, SWT.NONE);
		fd_text.left = new FormAttachment(lblDao, 6);
		lblDao.setAlignment(SWT.RIGHT);
		org.eclipse.swt.layout.FormData fd_lblDao = new org.eclipse.swt.layout.FormData();
		fd_lblDao.top = new FormAttachment(0, 13);
		fd_lblDao.left = new FormAttachment(0, 10);
		fd_lblDao.right = new FormAttachment(100, -393);
		lblDao.setLayoutData(fd_lblDao);
		lblDao.setText("\u5B9E\u4F53\u7C7B\u9650\u5B9A\u540D\uFF1A");
		
		Label label = new Label(shell, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		org.eclipse.swt.layout.FormData fd_label = new org.eclipse.swt.layout.FormData();
		fd_label.top = new FormAttachment(lblDao, 19);
		fd_label.left = new FormAttachment(lblDao, 0, SWT.LEFT);
		fd_label.right = new FormAttachment(100, -393);
		label.setLayoutData(fd_label);
		label.setText("\u6A21\u5757\u540D\uFF1A");
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				CodeGenerating.init(project.getProject().getLocation().toString());
				final String qualifiedName = text.getText();
				final String packageName = text_1.getText();
				if("".equals(qualifiedName)||"".equals(packageName)){
					MessageBox box = new MessageBox(shell, SWT.OK|SWT.ICON_WARNING);
					box.setText("��ʾ");
					box.setMessage("����д���޶�����ģ������");
					box.open();
				}else{
					shell.close();
					final Shell shellpb = new Shell(getParent(),SWT.BORDER|SWT.APPLICATION_MODAL|SWT.CLOSE);
					final PbDialog pbd = new PbDialog(shellpb);
					new Thread(new Runnable() {
						public void run() {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									pbd.open();
								}
							});
							try {
								CodeGenerating.generate(project,qualifiedName, packageName);
								project.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
							} catch (Exception e) {
								final String error = e.toString();
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										MessageBox box = new MessageBox(shellpb, SWT.OK|SWT.ICON_ERROR);
										box.setText("����");
										box.setMessage(error);
										box.open();
									}
								});
							}
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									pbd.close();
								}
							});
						}
					}).start();
				}
			}
		});
		org.eclipse.swt.layout.FormData fd_button = new org.eclipse.swt.layout.FormData();
		fd_button.left = new FormAttachment(0, 140);
		fd_button.top = new FormAttachment(text_1, 34);
		button.setLayoutData(fd_button);
		button.setText("\u786E\u5B9A");
		
		Button button_1 = new Button(shell, SWT.NONE);
		fd_button.right = new FormAttachment(100, -307);
		button_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		org.eclipse.swt.layout.FormData fd_button_1 = new org.eclipse.swt.layout.FormData();
		fd_button_1.top = new FormAttachment(button, 0, SWT.TOP);
		fd_button_1.left = new FormAttachment(button, 107);
		fd_button_1.right = new FormAttachment(100, -120);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("\u53D6\u6D88");
	}
}