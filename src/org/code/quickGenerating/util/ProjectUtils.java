package org.code.quickGenerating.util;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;

public class ProjectUtils {	
	public static IJavaProject getJavaProject(IWorkbenchWindow window){
		IJavaProject project = null;
		ISelectionService service = window.getSelectionService();
		ISelection selection = service.getSelection();
		if(selection instanceof IStructuredSelection){
			Object element = ((IStructuredSelection)selection).getFirstElement();
			if(element instanceof PackageFragmentRootContainer){
				project = ((PackageFragmentRootContainer)element).getJavaProject();    
			}else if(element instanceof IJavaElement){
				project = ((IJavaElement)element).getJavaProject();
			}
		}
		return project;
	}
}
