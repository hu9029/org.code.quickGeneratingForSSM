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
	private static String projectPath="";//项目路径
	
	static{
		//初始化freemarker配置
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
	 * @param qualifiedName 实体类限定名
	 * @param packageName 模块报名
	 * @param genSelect 选择生成代码,0--controller,1-service,2-dao,3-jsp
	 * @throws Exception
	 */
	public static void generate(IJavaProject project,String qualifiedName,String packageName,Boolean[] genSelect) 
			throws Exception{
		String[] strs = qualifiedName.split("\\.");
		
		String entity = strs[strs.length-1];//实体类名
		String daoName = entity+"Dao";//dao类名
		String servName = entity+"Serv";//service类名
		String controllerName = entity+"Controller";//controller类名
		String viewName = entity.substring(0, 1).toLowerCase()+entity.substring(1);//页面名称
		
		String basePackage = "";//基本包路径，到模块名
		String basePath = "";//基本文件路径,到模块名
		String projectPackage = "";//com.公司名,到公司名
		for(int i=0;i<4;i++){
			basePackage+=strs[i]+".";
			basePath+=File.separator+strs[i];
			if(i<3){
				projectPackage+=strs[i]+".";
			}
		}
		
		String daoPackage = basePackage+"dao";//dao包路径
		String servPackage = basePackage+"service";//service包路径
		String webPackage = basePackage+"web";//controller包路径
		
		String daoQualifiedName = daoPackage+"."+daoName;//dao限定名
		String servQualifiedName = servPackage+".I"+servName;//service接口限定名
		
		String path = "";
		if(!"".equals(projectPath)){
			path = projectPath+File.separator+"src"+File.separator+"main";
		}else{
			throw new IllegalArgumentException("工程路径为空");
		}
		
		String daoPath = path+File.separator+"java"+basePath+File.separator+"dao";
		String mapperPath = path+File.separator+"resources"+File.separator+"mybatis"+File.separator+packageName;
		String servicePath = path+File.separator+"java"+basePath+File.separator+"service";
		String webPath = path+File.separator+"java"+basePath+File.separator+"web";
		String viewPath = path+File.separator+"webapp"+File.separator+"WEB-INF"+File.separator+"jsp"+File.separator+packageName;
		
		Map<String,Object> data = new HashMap<String, Object>();//模板数据
		if(genSelect[2]){
			//生成dao类
			generateDao(daoPackage, qualifiedName, projectPackage, daoName, entity, daoPath, data);
			//生成Mybatis的Mapper文件
			generateDaoMapper(project, daoQualifiedName, mapperPath, data);
		}
		if(genSelect[1]){
			//生成service接口
			generateServInterface(servPackage,qualifiedName, projectPackage, servName, entity, servicePath, data);
			//生成service实现类
			generateServImpl(daoName, servicePath, daoQualifiedName, data);
		}
		if(genSelect[0]){
			//生成controller类
			generateController(webPackage, qualifiedName, projectPackage, packageName, controllerName, entity, servName, servQualifiedName, webPath, data);
		}
		if(genSelect[3]){
			//生成页面jsp
			generateView(project, qualifiedName, viewPath, viewName, data);
		}
		
	}
	
	/**生成dao接口
	 * @param daoPackage dao包路径
	 * @param qualifiedName 实体类限定名
	 * @param projectPackage 项目包路径
	 * @param daoName dao类名
	 * @param entity 实体类名
	 * @param daoPath dao文件路径
	 * @param data 模板数据
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
	 * 生成dao映射文件
	 * @param project 项目对象
	 * @param daoQualifiedName dao限定名
	 * @param mapperPath mapper文件路径
	 * @param data 模板数据
	 * @throws Exception
	 */
	private static void generateDaoMapper(IJavaProject project,String daoQualifiedName,
			String mapperPath,Map<String,Object> data)throws Exception{
		//生成Mybatis的Mapper文件
		List<EntityType> types = new ArrayList<EntityType>();
		//获得实体类属性名及类型
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
	 * 生成service接口
	 * @param servPackage service包路径
	 * @param qualifiedName 实体类限定名
	 * @param projectPackage 项目包路径
	 * @param servName service类名
	 * @param entity 实体类名
	 * @param servicePath service文件路径
	 * @param data 模板数据
	 * @throws Exception
	 */
	private static void generateServInterface(String servPackage,String qualifiedName,
			String projectPackage,String servName,String entity,String servicePath,
			Map<String,Object> data)throws Exception{
		data.put("package", servPackage);
		data.put("qualifiedName", qualifiedName);
		data.put("projectPackage", projectPackage);
		data.put("className", servName);
		data.put("entity", entity);
		data.put("selectId", "select"+entity+"Page");
		data.put("insertId", "insert"+entity);
		data.put("deleteId", "delete"+entity+"ById");
		data.put("updateId", "update"+entity+"ById");
		generateByFreeMarker("IService.ftl",servicePath,"I"+servName+".java",data);
	}
	/**
	 * 生成service实现类
	 * @param daoName dao类名，做为成员变量
	 * @param servicePath service文件路径
	 * @param daoQualifiedName dao限定名
	 * @param data 模板数据
	 * @throws Exception
	 */
	private static void generateServImpl(String daoName,String servicePath,String daoQualifiedName,Map<String,Object> data)throws Exception{
		data.put("daoQualifiedName", daoQualifiedName);
		data.put("propClass", daoName);
		generateByFreeMarker("Service.ftl",servicePath+File.separator+"impl",data.get("className")+"Impl.java",data);
	}
	/**
	 * 生成controller类
	 * @param webPackage controller包路径
	 * @param qualifiedName 实体类限定名
	 * @param projectPackage 项目包路径
	 * @param packageName 模块名
	 * @param controllerName controller类名
	 * @param entity 实体类名
	 * @param servName service类名
	 * @param servQualifiedName service接口限定名
	 * @param webPath controller文件路径
	 * @param data 模板数据
	 * @throws Exception
	 */
	private static void generateController(String webPackage,String qualifiedName,String projectPackage,
			String packageName,String controllerName,String entity,
			String servName,String servQualifiedName,String webPath,Map<String,Object> data)throws Exception{
		data.put("package", webPackage);
		data.put("qualifiedName", qualifiedName);
		data.put("projectPackage", projectPackage);
		data.put("packageName", packageName);//模块名
		data.put("className", controllerName);
		data.put("entity", entity);
		data.put("propClass", servName);
		data.put("servQualifiedName", servQualifiedName);
		data.put("updateId", "update"+entity+"ById");
		generateByFreeMarker("Controller.ftl",webPath,controllerName+".java",data);
	}
	
	/**
	 * 
	 * @param project 项目对象
	 * @param qualifiedName 实体类限定名
	 * @param viewPath view文件路径
	 * @param viewName view名称
	 * @param data 模板数据
	 * @throws Exception
	 */
	private static void generateView(IJavaProject project,String qualifiedName,String viewPath,String viewName,Map<String,Object> data)throws Exception{
		if(!data.containsKey("types")){
			//获得实体类属性名及类型
			List<EntityType> types = new ArrayList<EntityType>();
			Class clazz = ClassHelper.loadClass(project, qualifiedName);
			String[] propertys = CommonUtils.getClassFields(clazz);
			String[] propTypes = CommonUtils.getClassFieldsType(clazz);
			String[] columns = CommonUtils.getSqlFields(propertys);
			for(int i=0;i<propertys.length;i++){
				types.add(new EntityType(propertys[i], CommonUtils.isBaseType(propTypes[i]), columns[i]));
			}
			data.put("types", types);
		}
		data.put("viewName", viewName);
		generateByFreeMarker("view.ftl",viewPath,viewName+".jsp",data);
	}
	/**
	 * 
	 * @param name 模板名称
	 * @param dirPath 目录路径
	 * @param fileName 文件名
	 * @param data 模板数据
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
