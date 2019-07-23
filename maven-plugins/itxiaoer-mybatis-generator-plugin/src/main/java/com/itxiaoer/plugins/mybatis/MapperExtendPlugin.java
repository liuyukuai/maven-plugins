package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.commons.core.util.Lists;
import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Objects;

/**
 * mybatis 代码生成扩展（interface）
 *
 * @author : liuyk
 */
@Slf4j
@SuppressWarnings("unused")
public class MapperExtendPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        List<Method> methods = interfaze.getMethods();
        methods.forEach(e -> {
            if (!Objects.equals(e.getName(), "batchInsertSelective")) {
                e.addAnnotation("@Override");
            } else {
                e.addAnnotation("@SuppressWarnings(\"unused\")");
            }
            // 处理参数的问题
            JavaDocUtils.addJavaDoc(e);
        });

        interfaze.addAnnotation("@SuppressWarnings(\"ALL\")");
        // domain name
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        // key 

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        String shortName = "Long";
        if (Lists.iterable(primaryKeyColumns)) {
            shortName = primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName();
        }

        // exampleType
        String exampleType = introspectedTable.getExampleType();
        // interface
        String apiModelAnnotationPackage = properties.getProperty("interface");
        // import
        if (apiModelAnnotationPackage != null) {
            interfaze.addImportedType(new FullyQualifiedJavaType(apiModelAnnotationPackage));
            int i = apiModelAnnotationPackage.lastIndexOf(".");
            String simpleName = apiModelAnnotationPackage.substring(i + 1);
            apiModelAnnotationPackage = simpleName + "<" + domainObjectName + "," + shortName + "," + exampleType + ">";
            FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(apiModelAnnotationPackage);
            interfaze.addSuperInterface(fullyQualifiedJavaType);
        }

        String author = this.getProperties().getProperty("author");
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine("* " + domainObjectName + " " + domainObjectName);
        interfaze.addJavaDocLine("* ");
        interfaze.addJavaDocLine("* @author : " + (Objects.isNull(author) ? "liuyk" : author));
        interfaze.addJavaDocLine("*/");

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }
}
