package ${package};  
  
import java.util.List;
import java.util.Map;

import ${qualifiedName};
import ${projectPackage}utils.MyBatisRepository;

@MyBatisRepository
public interface ${className}{  
	public List<${entity}> ${selectId}(Map<String,Object> map);
    
    public void ${insertId}(${entity} <@lowerFC>${entity}</@lowerFC>);
    
    public void ${deleteId}(long id);
    
    public void ${updateId}(${entity} <@lowerFC>${entity}</@lowerFC>);
} 