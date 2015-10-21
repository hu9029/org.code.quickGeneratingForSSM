package ${package};  

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ${qualifiedName};
import ${servQualifiedName};
import ${projectPackage}utils.JsonUtils;
  
@Controller
@RequestMapping("/${packageName}/<@lowerFC>${entity}</@lowerFC>")
public class ${className}{
	@Autowired
	private ${"I"+propClass} <@lowerFC>${propClass}</@lowerFC>;
	
	@RequestMapping
	public String <@lowerFC>${entity}</@lowerFC>(){
		return "${packageName}/<@lowerFC>${entity}</@lowerFC>";
	}
	
	@RequestMapping(value = "/load",method = RequestMethod.GET)
	@ResponseBody
	public String load(@RequestParam int page,@RequestParam int rows,ServletRequest request){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", request.getParameter("name"));
		map.put("pageNo", page);
		map.put("pageSize", rows);
		return JsonUtils.toJsonString(<@lowerFC>${propClass}</@lowerFC>.get${entity}Page(map));
	}
	
	@RequestMapping(value = "/save",method = RequestMethod.POST)
	public void save(@ModelAttribute ${entity} <@lowerFC>${entity}</@lowerFC>,ServletResponse response) throws IOException{
		<@lowerFC>${entity}</@lowerFC>.setId(System.currentTimeMillis());
		<@lowerFC>${propClass}</@lowerFC>.save${entity}(<@lowerFC>${entity}</@lowerFC>);
		response.getWriter().write("success");
		
	}
	
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	public void update(@ModelAttribute ${entity} <@lowerFC>${entity}</@lowerFC>,ServletResponse response) throws IOException{
		<@lowerFC>${propClass}</@lowerFC>.${updateId}(<@lowerFC>${entity}</@lowerFC>);
		response.getWriter().write("success");
		
	}
	
	@RequestMapping(value = "/delete",method = RequestMethod.GET)
	@ResponseBody
	public String delete(@RequestParam long id){
		<@lowerFC>${propClass}</@lowerFC>.remove${entity}ById(id);
		return "success";
	}
} 