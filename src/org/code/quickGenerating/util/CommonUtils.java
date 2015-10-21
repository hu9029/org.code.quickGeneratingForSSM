package org.code.quickGenerating.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
	private static final char SEPARATOR = '_';

	public static String toUnderlineName(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			boolean nextUpperCase = true;

			if (i < (s.length() - 1)) {
				nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
			}

			if ((i >= 0) && Character.isUpperCase(c)) {
				if (!upperCase || !nextUpperCase) {
					if (i > 0)
						sb.append(SEPARATOR);
				}
				upperCase = true;
			} else {
				upperCase = false;
			}

			sb.append(Character.toUpperCase(c));
		}

		return sb.toString();
	}
	/**
	 * 判断给的类型是否是基础类型
	 * @param type 类限定名字符串
	 * @return true or false
	 */
	public static boolean isBaseType(String type){
		String[] baseTypes = { "int", "java.lang.Integer", "long",
				"java.lang.Long", "float", "java.lang.Float", "double",
				"java.lang.Double" };
		boolean result = false;
		for (int i = 0; i < baseTypes.length; i++) {
			result = result||baseTypes[i].equals(type);
		}
		return result;
	}

	public static String toCamelCase(String s) {
		if (s == null) {
			return null;
		}

		s = s.toLowerCase();

		StringBuilder sb = new StringBuilder(s.length());
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}
	/**
	 * 驼峰命名，首字母大写
	 * @param s
	 * @return
	 */
	public static String toCapitalizeCamelCase(String s) {
		if (s == null) {
			return null;
		}
		s = toCamelCase(s);
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	private static String[] getClassFieldsAndTypes(Class clazz,String type){
		Field[] fields = clazz.getDeclaredFields();
		List<String> names = new ArrayList<String>();
		for(int i=0;i<fields.length;i++){
			if("serialVersionUID".equals(fields[i].getName())){
				continue;
			}
			if("type".equals(type)){
				names.add(fields[i].getType().getName());
			}else{
				names.add(fields[i].getName());
			}
		}
		return names.toArray(new String[names.size()]);
	}
	
	public static String[] getClassFields(Class clazz){
		return getClassFieldsAndTypes(clazz,null);
	}
	
	public static String[] getClassFieldsType(Class clazz){
		return getClassFieldsAndTypes(clazz,"type");
	}
	
	public static String[] getSqlFields(String[] fields){
		String[] names = new String[fields.length];
		for(int i=0;i<fields.length;i++){
			names[i] = toUnderlineName(fields[i]);
		}
		return names;
	}
}
