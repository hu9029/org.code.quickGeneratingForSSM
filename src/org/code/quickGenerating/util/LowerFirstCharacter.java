package org.code.quickGenerating.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class LowerFirstCharacter implements TemplateDirectiveModel{

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		//没有参数及循环
		if  (!params.isEmpty()) {  
            throw new TemplateModelException(  
                    "This directive doesn't allow parameters." );  
        }  
        if(loopVars.length != 0 ) {  
                throw new TemplateModelException(  
                    "This directive doesn't allow loop variables." );  
        }
        
        if(body != null ){  
            body.render(new  UpperCaseFilterWriter(env.getOut()));  
        }else{  
            throw new RuntimeException( "missing body" );  
        } 
	}
	
	private static class UpperCaseFilterWriter extends Writer{
		private final Writer out;  
         
        protected UpperCaseFilterWriter (Writer out) {  
            this .out = out;  
        }
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			cbuf[0] = Character.toLowerCase(cbuf[ 0 ]);
			out.write(String.valueOf(cbuf).trim());
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void close() throws IOException {
			out.close();
		}
	}

}
