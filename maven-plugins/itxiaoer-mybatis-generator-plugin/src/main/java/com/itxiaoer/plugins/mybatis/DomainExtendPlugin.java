package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

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

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }
}
