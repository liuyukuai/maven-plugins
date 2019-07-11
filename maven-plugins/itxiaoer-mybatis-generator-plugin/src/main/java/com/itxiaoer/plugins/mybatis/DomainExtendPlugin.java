package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@SuppressWarnings("ALL")
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


        String validate = properties.getProperty("validate");

        if (!Objects.isNull(validate) && !validate.isEmpty()) {

            // 生成校验规则
            String javaProperty = introspectedColumn.getJavaProperty();
            boolean nullable = introspectedColumn.isNullable();
            int length = introspectedColumn.getLength();
            int scale = introspectedColumn.getScale();


            topLevelClass.addImportedType("javax.validation.constraints.Size");
            topLevelClass.addImportedType("javax.validation.constraints.NotBlank");
            topLevelClass.addImportedType("javax.validation.constraints.Digits");
            topLevelClass.addImportedType("javax.validation.constraints.NotEmpty");

            if (!Objects.equals("id", javaProperty)) {
                // 不能为空


                String jdbcTypeName = introspectedColumn.getJdbcTypeName();
                if (Objects.equals(jdbcTypeName, "BIGINT") || Objects.equals(jdbcTypeName, "INTEGER") || Objects.equals("DECIMAL", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotEmpty(message = \"" + introspectedColumn.getRemarks() + "不能为空\")");
                    }
                    field.addAnnotation(" @Digits(integer = " + length + ", fraction = " + scale + ", message = \"" + introspectedColumn.getRemarks() + "只能为数字\")");
                }

                if (Objects.equals("VARCHAR", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotBlank(message = \"" + introspectedColumn.getRemarks() + "不能为空\")");
                    }
                    field.addAnnotation("@Size(max = " + length + ", message = \"" + introspectedColumn.getRemarks() + "不能超过" + length + "位\")");
                }

                if (Objects.equals("DATE", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotEmpty(message = \"" + introspectedColumn.getRemarks() + "不能为空\")");
                    }
                    field.addAnnotation("@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN, timezone = \"GMT+8\")");
                }
            }
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }


}
