package com.itxiaoer.plugins.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

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
            int var7 = remarkLines.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                String remarkLine = remarkLines[var8];
                field.addJavaDocLine(" *   " + remarkLine);
            }
        }

        field.addJavaDocLine(" *");
        field.addJavaDocLine(" */");
    }
}
