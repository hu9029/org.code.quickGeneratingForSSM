package org.code.quickGenerating.view;

import org.code.quickGenerating.CodeGenerating;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CodeGenerateView extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text daoText;
	private Text modelText;
	private IJavaProject project;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CodeGenerateView(IJavaProject project,Shell parent, int style) {
		super(parent, style);
		this.project = project;
		setText("代码生成");
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
		shell.setSize(517, 252);
		shell.setText("代码生成");
		shell.setLayout(new org.eclipse.swt.layout.FormLayout());
		
		daoText = new Text(shell, SWT.BORDER);
		org.eclipse.swt.layout.FormData fd_daoText = new org.eclipse.swt.layout.FormData();
		fd_daoText.right = new FormAttachment(100, -23);
		fd_daoText.top = new FormAttachment(0, 10);
		daoText.setLayoutData(fd_daoText);
		
		modelText = new Text(shell, SWT.BORDER);
		org.eclipse.swt.layout.FormData fd_modelText = new org.eclipse.swt.layout.FormData();
		fd_modelText.left = new FormAttachment(daoText, 0, SWT.LEFT);
		fd_modelText.top = new FormAttachment(daoText, 13);
		fd_modelText.right = new FormAttachment(100, -23);
		modelText.setLayoutData(fd_modelText);
		
		Label daoLabel = new Label(shell, SWT.NONE);
		fd_daoText.left = new FormAttachment(daoLabel, 6);
		daoLabel.setAlignment(SWT.RIGHT);
		org.eclipse.swt.layout.FormData fd_daoLabel = new org.eclipse.swt.layout.FormData();
		fd_daoLabel.top = new FormAttachment(0, 13);
		fd_daoLabel.left = new FormAttachment(0, 10);
		fd_daoLabel.right = new FormAttachment(100, -393);
		daoLabel.setLayoutData(fd_daoLabel);
		daoLabel.setText("实体类限定名");
		
		Label modelLabel = new Label(shell, SWT.NONE);
		modelLabel.setAlignment(SWT.RIGHT);
		org.eclipse.swt.layout.FormData fd_modelLabel = new org.eclipse.swt.layout.FormData();
		fd_modelLabel.top = new FormAttachment(daoLabel, 19);
		fd_modelLabel.left = new FormAttachment(daoLabel, 0, SWT.LEFT);
		fd_modelLabel.right = new FormAttachment(100, -393);
		modelLabel.setLayoutData(fd_modelLabel);
		modelLabel.setText("模块名");
		
		Button confirmButton = new Button(shell, SWT.NONE);
		confirmButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				CodeGenerating.init(project.getProject().getLocation().toString());
				final String qualifiedName = daoText.getText();
				final String packageName = modelText.getText();
				final Boolean[] genSelect = new Boolean[4];
				if("".equals(qualifiedName)||"".equals(packageName)){
					MessageBox box = new MessageBox(shell, SWT.OK|SWT.ICON_WARNING);
					box.setText("提示");
					box.setMessage("请填写类限定名及模块名称");
					box.open();
				}else{
					Control[] widgets= shell.getChildren();
					for (Control widget : widgets) {
						if(widget instanceof Button){
							Button btn = (Button)widget;
							if(btn.getText().equals("Controller")){
								//是否生成controller
								genSelect[0] = btn.getSelection();
							}else if(btn.getText().equals("Service")){
								//是否生成Service
								genSelect[1] = btn.getSelection();
							}else if(btn.getText().equals("Dao")){
								//是否生成Dao
								genSelect[2] = btn.getSelection();
							}else if(btn.getText().equals("Jsp")){
								//是否生成Jsp
								genSelect[3] = btn.getSelection();
							}
						}
					}
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
								CodeGenerating.generate(project,qualifiedName, packageName,genSelect);
								project.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
							} catch (Exception e) {
								final String error = e.toString();
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										MessageBox box = new MessageBox(shellpb, SWT.OK|SWT.ICON_ERROR);
										box.setText("错误");
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
		org.eclipse.swt.layout.FormData fd_confirmButton = new org.eclipse.swt.layout.FormData();
		fd_confirmButton.left = new FormAttachment(0, 173);
		fd_confirmButton.bottom = new FormAttachment(100, -23);
		confirmButton.setLayoutData(fd_confirmButton);
		confirmButton.setText("确定");
		
		Button cancelButton = new Button(shell, SWT.NONE);
		fd_confirmButton.right = new FormAttachment(cancelButton, -99);
		
		org.eclipse.swt.layout.FormData fd_cancelButton = new org.eclipse.swt.layout.FormData();
		fd_cancelButton.bottom = new FormAttachment(100, -23);
		fd_cancelButton.right = new FormAttachment(100, -79);
		fd_cancelButton.left = new FormAttachment(0, 352);
		cancelButton.setLayoutData(fd_cancelButton);
		cancelButton.setText("取消");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		Button controllerCheckBtn = new Button(shell, SWT.CHECK);
		controllerCheckBtn.setSelection(true);
		FormData fd_controllerCheckBtn = new FormData();
		fd_controllerCheckBtn.top = new FormAttachment(modelText, 21);
		fd_controllerCheckBtn.left = new FormAttachment(daoText, 0, SWT.LEFT);
		controllerCheckBtn.setLayoutData(fd_controllerCheckBtn);
		controllerCheckBtn.setText("Controller");
		
		Button serviceCheckBtn = new Button(shell, SWT.CHECK);
		serviceCheckBtn.setAlignment(SWT.CENTER);
		serviceCheckBtn.setText("Service");
		serviceCheckBtn.setSelection(true);
		FormData fd_serviceCheckBtn = new FormData();
		fd_serviceCheckBtn.top = new FormAttachment(controllerCheckBtn, 0, SWT.TOP);
		fd_serviceCheckBtn.left = new FormAttachment(controllerCheckBtn, 20);
		serviceCheckBtn.setLayoutData(fd_serviceCheckBtn);
		
		Button daoCheckBtn = new Button(shell, SWT.CHECK);
		daoCheckBtn.setText("Dao");
		daoCheckBtn.setSelection(true);
		FormData fd_daoCheckBtn = new FormData();
		fd_daoCheckBtn.top = new FormAttachment(controllerCheckBtn, 0, SWT.TOP);
		fd_daoCheckBtn.left = new FormAttachment(serviceCheckBtn, 29);
		daoCheckBtn.setLayoutData(fd_daoCheckBtn);
		
		Button JspCheckBtn = new Button(shell, SWT.CHECK);
		JspCheckBtn.setAlignment(SWT.CENTER);
		JspCheckBtn.setText("Jsp");
		JspCheckBtn.setSelection(true);
		FormData fd_JspCheckBtn = new FormData();
		fd_JspCheckBtn.top = new FormAttachment(controllerCheckBtn, 0, SWT.TOP);
		fd_JspCheckBtn.left = new FormAttachment(daoCheckBtn, 24);
		JspCheckBtn.setLayoutData(fd_JspCheckBtn);
	}
}
