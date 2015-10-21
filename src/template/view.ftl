<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="/WEB-INF/shiro.tld"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/easyUI.jsp" %>
<style type="text/css">
	.add {background-image: url("../img/common/add.png");}
	.delete {background-image: url("../img/common/delete.png");}
	.search {background-image: url("../img/common/search.png");}
	.update {background-image: url("../img/common/update.png");}
</style>
<title>title</title>
</head>
<body>
	<div id="tbar" style="height: 25px;">
		<div style="margin-left: 10px;">
			<input class="easyui-searchbox" id="parameter" data-options="prompt:'�������ѯ����',searcher:searchData" name="parameter"/>
			<shiro:hasPermission name="${viewName}:create">
			<a class="easyui-linkbutton" data-options="plain:true,iconCls:'add'" onclick="add();">����</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="${viewName}:update">
			<a class="easyui-linkbutton" data-options="plain:true,iconCls:'update'" onclick="edit();">�޸�</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="${viewName}:delete">
			<a class="easyui-linkbutton" data-options="plain:true,iconCls:'delete'" onclick="destroy();">ɾ��</a>
			</shiro:hasPermission>
		</div>
	</div>
	<shiro:hasAnyPermissions name="${viewName}:update,${viewName}:create">
	<div id="dlg" class="easyui-dialog" style="width:350px;height:300px;overflow:auto;"
            closed="true" modal="true" buttons="#dlg-buttons">
    	 <form id="fm" method="post" novalidate>
    	 	 <#list types as type>
    	 	 <div class="fitem">
    	 	 	<label>${type.property}:</label>
    	 	 	<input name="${type.property}" id="${type.property}" prompt="����${type.property}" class="easyui-textbox" data-options="validType:{length:[0,30]}" required="true" missingMessage="������${type.property}.">
    	 	 </div>
    	 	 </#list>
         </form>
         <div id="dlg-buttons">
	        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" onclick="save()" style="width:90px">����</a>
	        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="$('#dlg').dialog('close');" style="width:90px">ȡ��</a>
	    </div>
    </div>
    </shiro:hasAnyPermissions>
	<table id="dg" class="easyui-datagrid" rownumbers="true" toolbar="#tbar"  
   		   url="${viewName}/load" method="get" pagination="true"
   		   pageSize="30" data-options="emptyMsg:'û�в�ѯ������',border:false,
   				singleSelect:true,fit:true,fitColumns:true">
	    <thead>
	        <tr>
	        	<#list types as type>
	        	<th data-options="field:'${type.property}'" width="100">${type.property}</th>
	        	</#list>
			</tr>
		</thead>
	</table>
</body>
<style type="text/css">
	#fm{
	    margin:0;
	    padding:30px 30px;
	}
	.fitem{
	    margin-bottom:5px;
	}
	.fitem label{
	    display:inline-block;
	    width:60px;
	    text-align: right;
	}
	.fitem input{
	    width:160px;
	}
</style>
<script type="text/javascript">
var url;
/**
 * ����
 */
<shiro:hasPermission name="${viewName}:create">
function add(){
	$('#dlg').dialog('open').dialog('setTitle','����');
    $('#fm').form('clear');
    url = '${viewName}/save';
}
</shiro:hasPermission>
/**
 * ����
 */
<shiro:hasPermission name="${viewName}:update">
function edit(){
    var row = $('#dg').datagrid('getSelected');
    if (row){
        var id = row.id;
        $('#fm').form('clear');
    	$('#dlg').dialog('open').dialog('setTitle','�༭');
        $('#fm').form('load',row);
        url='${viewName}/update?id='+id;
    }else{
    	$.messager.alert('��ʾ','��ѡ��һ�в�������');
    }
}
</shiro:hasPermission>
/**
 * ����
 */
<shiro:hasAnyPermissions name="${viewName}:update,${viewName}:create">
function save(){
	$('#fm').form('submit',{
        url: url,
        onSubmit: function(param){
        	var isValid = $(this).form('validate');
			 if(isValid){
				 $.messager.progress({
		                title:'���Ժ�',
		                msg:'���ݱ�����...'
		            });
			 }        		
            return isValid;
        },
        success: function(result){
       	 $.messager.progress('close');
            if (result=='success'){
           	 	$('#dlg').dialog('close');
                $('#dg').datagrid('reload');
            }else{
            	 $.messager.alert('��ʾ',result,'error');
            }
        }
    });
}
</shiro:hasAnyPermissions>
/**
 * ɾ��
 */
<shiro:hasPermission name="${viewName}:delete">
function destroy(){
    var row = $('#dg').datagrid('getSelected');
    if (row){
    	var id = row.id;
        $.messager.confirm('��ʾ','ȷ��Ҫɾ������Ϣ?',function(r){
        	if(r){
	        	$.messager.progress({
	                title:'���Ժ�',
	                msg:'����ɾ��...'
	            });
		        $.ajax({
		        	url:'${viewName}/delete?id='+id,
		        	dataType:"text",
		    		success:function(data){
		    			$.messager.progress('close');
		    			if (data=='success'){
		            	 	$('#dlg').dialog('close');
		                	$('#dg').datagrid('reload');
		                }else{
		                	$.messager.alert('��ʾ',data,'error');
		                }
		    		},
		    		error:function(request,msg){
		    			$.messager.progress('close');
		    			$.messager.alert('��ʾ','ɾ��ʧ��:'+msg,'error');
		    		}
		        });
        	}
        });
    }else{
    	$.messager.alert('��ʾ','��ѡ��һ�в�������');
    }
}
</shiro:hasPermission>
/**
 * ��ѯ
 */
function searchData(value){
	$('#dg').datagrid('load',{
		'name': value
	});
}
</script>
<link rel="stylesheet" type="text/css" href="${"$"}{pageContext.request.contextPath }/js/jqueryEasyUI/themes/icon.css">
</html>