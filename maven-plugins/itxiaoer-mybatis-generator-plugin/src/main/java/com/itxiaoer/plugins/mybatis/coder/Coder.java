package com.itxiaoer.plugins.mybatis.coder;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.Context;

import java.util.Properties;

/**
 * 代码生成器
 *
 * @author : liuyk
 */
public interface Coder {
    /**
     * 生成对应的类型
     *
     * @param properties        properties
     * @param entityPackage     entityPackage
     * @param introspectedTable introspectedTable
     * @param context           context
     * @return javaFile
     */
    GeneratedJavaFile generator(Context context, IntrospectedTable introspectedTable, Properties properties, String entityPackage);

    /**
     * 首字母小写
     *
     * @param s 字符串
     * @return string
     */
    default String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

}
