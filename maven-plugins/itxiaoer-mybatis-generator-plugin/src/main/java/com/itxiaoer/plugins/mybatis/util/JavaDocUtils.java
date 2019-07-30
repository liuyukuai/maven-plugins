package com.itxiaoer.plugins.mybatis.util;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.List;
import java.util.Objects;

/**
 * @author : liuyk
 */

public final class JavaDocUtils {

    public static void addJavaDoc(Method method) {

        // 处理参数的问题
        List<Parameter> parameters = method.getParameters();
        List<String> javaDocLines = method.getJavaDocLines();
        javaDocLines.clear();
        javaDocLines.add("/**");
        javaDocLines.add("* " + method.getName());
        javaDocLines.add("* ");
        parameters.forEach(p -> 
            javaDocLines.add("* @param " + p.getName() + " " + p.getName())
        );
        FullyQualifiedJavaType returnType = method.getReturnType();
        if (!Objects.isNull(returnType)) {
            javaDocLines.add("* @return " + returnType.getFullyQualifiedNameWithoutTypeParameters());
        }
        javaDocLines.add("* ");
        javaDocLines.add("**/");
    }
}
