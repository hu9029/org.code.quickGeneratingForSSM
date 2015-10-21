<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${daoQualifiedName}">
  <resultMap id="<@lowerFC>${entity}</@lowerFC>" type="${entity}">
  	<#list types as type>
    <result column="${type.column}" property="${type.property}"/>
    </#list>
  </resultMap>
  
  <sql id="${entity}_Where_Clause">
    <where>
      <#list types as type>
      <#if type.baseType>
      <if test="@${projectPackage}utils.Assert@notEqual(${type.property},0)">AND ${type.column} = ${"#"}{${type.property}}</if>
      <#else>
      <if test="@${projectPackage}utils.Assert@notEmpty(${type.property})">AND ${type.column} = ${"#"}{${type.property}}</if>
      </#if>
      </#list>
    </where>
  </sql>
  
  <sql id="${entity}_Set_Clause">
    <set>
      <#list types as type>
      <#if type.baseType>
      <if test="@${projectPackage}utils.Assert@notEqual(${type.property},0)">${type.column} = ${"#"}{${type.property}},</if>
      <#else>
      <if test="@${projectPackage}utils.Assert@notEmpty(${type.property})">${type.column} = ${"#"}{${type.property}},</if>
      </#if>
      </#list>
    </set>
  </sql>
  
  <insert id="${insertId}" parameterType="${entity}">
	INSERT INTO ${table} (<#list types as type><#if (types?size==type_index+1)>${type.column}<#else>${type.column},</#if></#list>)
	VALUES(<#list types as type><#if (types?size==type_index+1)>${"#"}{${type.property}}<#else>${"#"}{${type.property}},</#if></#list>)
  </insert>
  
  <select id="${selectId}" parameterType="java.util.Map" resultMap="<@lowerFC>${entity}</@lowerFC>">
	SELECT <#list types as type><#if (types?size==type_index+1)>${type.column}<#else>${type.column},</#if></#list>
	FROM ${table}
    <include refid="${entity}_Where_Clause"/>
  </select>
  
  <update id="${updateId}" parameterType="${entity}">
	UPDATE ${table}
    <include refid="${entity}_Set_Clause"/>
	WHERE ID=${"#"}{id}
  </update>
  
  <delete id="${deleteId}">
	DELETE FROM ${table}
	WHERE ID=${"#"}{id}
  </delete>
</mapper>