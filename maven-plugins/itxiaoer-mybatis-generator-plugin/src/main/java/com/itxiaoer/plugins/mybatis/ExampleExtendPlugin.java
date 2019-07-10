package com.itxiaoer.plugins.mybatis;


import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 添加example接口
 *
 * @author : liuyk
 */
@SuppressWarnings("unused")
public class ExampleExtendPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 属性处理
        List<Field> fields = topLevelClass.getFields();

        for (Field field : fields) {
            // 属性可见性修改为private
            field.setVisibility(JavaVisibility.PRIVATE);
        }

        // interface
        String interfaceClass = properties.getProperty("interface");

        // 需要重写的方法，逗号隔开
        String methodsNames = properties.getProperty("methods");

        // 判断是否为空
        if (!Objects.isNull(methodsNames)) {
            List<Method> methods = topLevelClass.getMethods();
            List<String> names = Stream.of(methodsNames.split(",")).collect(Collectors.toList());
            for (Method method : methods) {
                if (names.contains(method.getName())) {
                    method.addAnnotation("@Override");
                }
                // 添加参数
                JavaDocUtils.addJavaDoc(method);
            }
        }

        // 添加接口
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(interfaceClass));
        // 添加注解
        topLevelClass.addAnnotation("@SuppressWarnings(\"ALL\")");
        // 修改内部
        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();

        for (InnerClass innerClass : innerClasses) {
            innerClass.setAbstract(false);
        }


        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }


}
