package com.itxiaoer.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.Set;

/**
 * @author : liuyk
 */
@SuppressWarnings("unused")
public class CommentGenerator extends DefaultCommentGenerator {

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        field.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                field.addJavaDocLine(" *   " + remarkLine);
            }
        }

        field.addJavaDocLine(" *");
        field.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + field.getName());
        field.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        sb.append(introspectedTable.getFullyQualifiedTable());
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        super.addFieldAnnotation(field, introspectedTable, imports);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        super.addFieldAnnotation(field, introspectedTable, introspectedColumn, imports);
    }
}
