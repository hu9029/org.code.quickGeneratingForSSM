package org.code.quickGenerating.util;

public class EntityType {
	private String property;//属性名称
	private boolean baseType;//属性类型
	private String column;//属性对应表字段
	
	public EntityType(String property, boolean baseType, String column) {
		this.property = property;
		this.baseType = baseType;
		this.column = column;
	}
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public boolean getBaseType() {
		return baseType;
	}
	public void setBaseType(boolean baseType) {
		this.baseType = baseType;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
}
