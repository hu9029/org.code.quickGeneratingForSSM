package org.code.quickGenerating;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.code.quickGenerating.util.ClassHelper;
import org.code.quickGenerating.util.CommonUtils;
import org.code.quickGenerating.util.EntityType;
import org.code.quickGenerating.util.LowerFirstCharacter;
import org.eclipse.jdt.core.IJavaProject;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class CodeGenerating {
	public static Configuration cfg;
	private static String projectPath="";//��Ŀ·��
	
	static{
		//��ʼ��freemarker����
		cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setClassLoaderForTemplateLoading(CodeGenerating.class.getClassLoader(), "/template");
		cfg.setSharedVariable("lowerFC", new LowerFirstCharacter());
	}
	
	public static void init(String projectPath){
		CodeGenerating.projectPath = projectPath;
	}
	
	/**
	 * 
	 * @param project
	 * @param qualifiedName ʵ�����޶���
	 * @param packageName ģ�鱨��
	 * @throws Exception
	 */
	public static void generate(IJavaProject project,String qualifiedName,String packageName) 
			throws Exception{
		String[] strs = qualifiedName.split("\\.");
		
		String entity = strs[strs.length-1];//ʵ������
		String daoName = entity+"Dao";//dao����
		String servName = entity+"Serv";//service����
		String controllerName = entity+"Controller";//controller����
		String viewName = entity.substring(0, 1).toLowerCase()+entity.substring(1);//ҳ������
		
		String basePackage = "";//������·������ģ����
		String basePath = "";//�����ļ�·��,��ģ����
		String projectPackage = "";//com.��˾��,����˾��
		for(int i=0;i<4;i++){
			basePackage+=strs[i]+".";
			basePath+=File.separator+strs[i];
			if(i<3){
				projectPackage+=strs[i]+".";
			}
		}
		
		String daoPackage = basePackage+"dao";//dao��·��
		String servPackage = basePackage+"service";//service��·��
		String webPackage = basePackage+"web";//controller��·��
		
		String daoQualifiedName = daoPackage+"."+daoName;//dao�޶���
		String servQualifiedName = servPackage+".I"+servName;//service�ӿ��޶���
		
		String path = "";
		if(!"".equals(projectPath)){
			path = projectPath+File.separator+"src"+File.separator+"main";
		}else{
			throw new IllegalArgumentException("����·��Ϊ��");
		}
		
		String daoPath = path+File.separator+"java"+basePath+File.separator+"dao";
		String mapperPath = path+File.separator+"resources"+File.separator+"mybatis"+File.separator+packageName;
		String servicePath = path+File.separator+"java"+basePath+File.separator+"service";
		String webPath = path+File.separator+"java"+basePath+File.separator+"web";
		String viewPath = path+File.separator+"webapp"+File.separator+"WEB-INF"+File.separator+"jsp"+File.separator+packageName;
		
		Map<String,Object> data = new HashMap<String, Object>();//ģ������
		//����dao��
		generateDao(daoPackage, qualifiedName, projectPackage, daoName, entity, daoPath, data);
		//����Mybatis��Mapper�ļ�
		generateDaoMapper(project, daoQualifiedName, mapperPath, data);
		//����service�ӿ�
		generateServInterface(servPackage, servName, servicePath, data);
		//����serviceʵ����
		generateServImpl(daoName, servicePath, data);
		//����controller��
		generateController(webPackage, packageName, controllerName, servName, servQualifiedName, webPath, data);
		//����ҳ��jsp
		generateView(viewPath, viewName, data);
		
	}
	
	/**����dao�ӿ�
	 * @param daoPackage dao��·��
	 * @param qualifiedName ʵ�����޶���
	 * @param projectPackage ��Ŀ��·��
	 * @param daoName dao����
	 * @param entity ʵ������
	 * @param daoPath dao�ļ�·��
	 * @param data ģ������
	 * @throws Exception
	 */
	private static void generateDao(String daoPackage,String qualifiedName,String projectPackage,
			String daoName,String entity,String daoPath,Map<String,Object> data) throws Exception{
		data.put("package", daoPackage);
		data.put("qualifiedName", qualifiedName);
		data.put("projectPackage", projectPackage);
		data.put("className", daoName);
		data.put("entity", entity);
		data.put("selectId", "select"+entity+"Page");
		data.put("insertId", "insert"+entity);
		data.put("deleteId", "delete"+entity+"ById");
		data.put("updateId", "update"+entity+"ById");
		generateByFreeMarker("IDao.ftl",daoPath,daoName+".java",data);
	}
	/**
	 * ����daoӳ���ļ�
	 * @param project ��Ŀ����
	 * @param daoQualifiedName dao�޶���
	 * @param mapperPath mapper�ļ�·��
	 * @param data ģ������
	 * @throws Exception
	 */
	private static void generateDaoMapper(IJavaProject project,String daoQualifiedName,
			String mapperPath,Map<String,Object> data)throws Exception{
		//����Mybatis��Mapper�ļ�
		List<EntityType> types = new ArrayList<EntityType>();
		//���ʵ����������������
		Class clazz = ClassHelper.loadClass(project, (String)data.get("qualifiedName"));
		String[] propertys = CommonUtils.getClassFields(clazz);
		String[] propTypes = CommonUtils.getClassFieldsType(clazz);
		String[] columns = CommonUtils.getSqlFields(propertys);
		for(int i=0;i<propertys.length;i++){
			types.add(new EntityType(propertys[i], CommonUtils.isBaseType(propTypes[i]), columns[i]));
		}
		data.put("types", types);
		data.put("daoQualifiedName", daoQualifiedName);
		data.put("table", CommonUtils.toUnderlineName((String)data.get("entity")));
		generateByFreeMarker("Mapper.ftl",mapperPath,data.get("entity")+"Mapper.xml",data);
	}
	/**
	 * ����service�ӿ�
	 * @param servPackage service��·��
	 * @param servName service����
	 * @param servicePath service�ļ�·��
	 * @param data ģ������
	 * @throws Exception
	 */
	private static void generateServInterface(String servPackage,String servName,String servicePath,
			Map<String,Object> data)throws Exception{
		data.put("package", servPackage);
		data.put("className", servName);
		generateByFreeMarker("IService.ftl",servicePath,"I"+servName+".java",data);
	}
	/**
	 * ����serviceʵ����
	 * @param daoName dao��������Ϊ��Ա����
	 * @param servicePath service�ļ�·��
	 * @param data ģ������
	 * @throws Exception
	 */
	private static void generateServImpl(String daoName,String servicePath,Map<String,Object> data)throws Exception{
		data.put("propClass", daoName);
		generateByFreeMarker("Service.ftl",servicePath+File.separator+"impl",data.get("className")+"Impl.java",data);
	}
	/**
	 * ����controller��
	 * @param webPackage controller��·��
	 * @param packageName ģ����
	 * @param controllerName controller����
	 * @param servName service����
	 * @param servQualifiedName service�ӿ��޶���
	 * @param webPath controller�ļ�·��
	 * @param data ģ������
	 * @throws Exception
	 */
	private static void generateController(String webPackage,String packageName,String controllerName,
			String servName,String servQualifiedName,String webPath,Map<String,Object> data)throws Exception{
		data.put("package", webPackage);
		data.put("packageName", packageName);//ģ����
		data.put("className", controllerName);
		data.put("propClass", servName);
		data.put("servQualifiedName", servQualifiedName);
		generateByFreeMarker("Controller.ftl",webPath,controllerName+".java",data);
	}
	
	private static void generateView(String viewPath,String viewName,Map<String,Object> data)throws Exception{
		data.put("viewName", viewName);
		generateByFreeMarker("view.ftl",viewPath,viewName+".jsp",data);
	}
	/**
	 * 
	 * @param name ģ������
	 * @param dirPath Ŀ¼·��
	 * @param fileName �ļ���
	 * @param data ģ������
	 * @throws Exception
	 */
	private static void generateByFreeMarker(String name,String dirPath,String fileName,Map<String,Object> data) throws Exception{
		File dir = new File(dirPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File file = new File(dirPath+File.separator+fileName);
		Template t = cfg.getTemplate(name);
		t.process(data, new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
	}
}