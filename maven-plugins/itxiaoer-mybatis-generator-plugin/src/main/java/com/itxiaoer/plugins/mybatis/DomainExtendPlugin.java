package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Objects;

/**
 * mybatis 代码生成扩展（javadoc）
 *
 * @author : liuyk
 */
@SuppressWarnings("unused")
public class DomainExtendPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addAnnotation("@SuppressWarnings(\"unused\")");

        List<Method> methods = topLevelClass.getMethods();
        for (Method method : methods) {
            JavaDocUtils.addJavaDoc(method);
        }

        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();

        for (InnerClass innerClass : innerClasses) {
            topLevelClass.addAnnotation("@SuppressWarnings(\"ALL\")");
        }

        List<InnerEnum> innerEnums = topLevelClass.getInnerEnums();

        for (InnerEnum innerEnum : innerEnums) {
            innerEnum.addAnnotation("@SuppressWarnings(\"ALL\")");
        }



        String author = this.getProperties().getProperty("author");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine("* " + introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine("* ");
        topLevelClass.addJavaDocLine("* @author : " + (Objects.isNull(author) ? "liuyk" : author));
        topLevelClass.addJavaDocLine("*/");

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {

        String classAnnotation = "@ApiModel(value=\"" + introspectedTable.getRemarks() + "\")";
        if (!topLevelClass.getAnnotations().contains(classAnnotation)) {
            topLevelClass.addAnnotation(classAnnotation);
        }
        String apiModelAnnotationPackage = this.properties.getProperty("apiModelAnnotationPackage");
        String apiModelPropertyAnnotationPackage = this.properties.getProperty("apiModelPropertyAnnotationPackage");
        if (Objects.isNull(apiModelAnnotationPackage) || apiModelPropertyAnnotationPackage.isEmpty()) {
            apiModelAnnotationPackage = "io.swagger.annotations.ApiModel";
        }
        if (Objects.isNull(apiModelPropertyAnnotationPackage) || apiModelPropertyAnnotationPackage.isEmpty()) {
            apiModelPropertyAnnotationPackage = "io.swagger.annotations.ApiModelProperty";
        }
        topLevelClass.addImportedType(apiModelAnnotationPackage);
        topLevelClass.addImportedType(apiModelPropertyAnnotationPackage);
        field.addAnnotation("@ApiModelProperty(value=\"" + introspectedColumn.getRemarks() +
                "\",name=\"" + introspectedColumn.getJavaProperty() +
                "\",dataType=\"" + introspectedColumn.getFullyQualifiedJavaType().getShortName() +
                "\")");

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }



}
