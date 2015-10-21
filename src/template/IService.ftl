package ${package};  
  
import java.util.Map;

import ${qualifiedName};
import ${projectPackage}utils.PageBean;
  
public interface ${"I"+className}{  
	public PageBean<${entity}> get${entity}Page(Map<String,Object> map);
   
    public void save${entity}(${entity} <@lowerFC>${entity}</@lowerFC>);
    
    public void remove${entity}ById(long id);
    
    public void ${updateId}(${entity} <@lowerFC>${entity}</@lowerFC>);
}