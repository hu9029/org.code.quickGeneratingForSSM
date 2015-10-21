package ${package}.impl;  
  
import ${daoQualifiedName};
import ${package}.${"I"+className};
import ${qualifiedName};
import ${projectPackage}utils.PageBean;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
  
@Service
public class ${className+"Impl"} implements ${"I"+className}{  
	@Autowired
	private ${propClass} <@lowerFC>${propClass}</@lowerFC>;
	
	@Override
	public PageBean<${entity}> get${entity}Page(Map<String,Object> map){
		PageBean<${entity}> pageBean = new PageBean<${entity}>();
		pageBean.setPageNo((Integer)map.get("pageNo"));
		pageBean.setPageSize((Integer)map.get("pageSize"));
		map.put("pageBean", pageBean);
		map.put("needCount", true);
		pageBean.setRows(<@lowerFC>${propClass}</@lowerFC>.${selectId}(map));
		return pageBean;
	}
	
	@Override
    public void save${entity}(${entity} <@lowerFC>${entity}</@lowerFC>){
    	<@lowerFC>${propClass}</@lowerFC>.${insertId}(<@lowerFC>${entity}</@lowerFC>);
    }
    
    @Override
    public void remove${entity}ById(long id){
    	<@lowerFC>${propClass}</@lowerFC>.${deleteId}(id);
    }
    
    @Override
    public void ${updateId}(${entity} <@lowerFC>${entity}</@lowerFC>){
    	<@lowerFC>${propClass}</@lowerFC>.${updateId}(<@lowerFC>${entity}</@lowerFC>);
    }
} 