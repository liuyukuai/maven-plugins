package com.itxiaoer.plugins.mybatis;


import com.itxiaoer.commons.core.util.Lists;
import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import org.mybatis.generator.api.IntrospectedColumn;
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

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        String shortName = "Long";
        if (Lists.iterable(primaryKeyColumns)) {
            shortName = primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName();
        }


        // interface
        String interfaceClass = properties.getProperty("interface") + "<" + shortName + ">";

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

        // 添加ids方法
        Method method = new Method();

        method.addAnnotation("@Override");
        method.setReturnType(topLevelClass.getType());
        method.setName("ids");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + shortName + ">"), "ids"));
        method.addBodyLine("this.createCriteria().andIdIn(ids);");
        method.addBodyLine("return this;");
        topLevelClass.addMethod(method);


        // 添加orgCodes方法
        method = new Method();

        method.addAnnotation("@Override");
        method.setReturnType(topLevelClass.getType());
        method.setName("orgCodes");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "orgCodes"));
        method.addBodyLine("this.createCriteria().andOrgCodeIn(orgCodes);");
        method.addBodyLine("return this;");
        topLevelClass.addMethod(method);


        //添加分页方法
        topLevelClass.addImportedType("java.util.*");
        topLevelClass.addImportedType("java.util.stream.Collectors");
        topLevelClass.addImportedType("net.tsingyun.commons.core.page.Paging");
        topLevelClass.addImportedType("net.tsingyun.commons.core.page.Sort");
        topLevelClass.addImportedType("net.tsingyun.commons.core.util.Lists");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.util.PagingUtils");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.PageRequest");


        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        String targetPackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        topLevelClass.addStaticImport(targetPackage + "." + domainObjectName + ".Column");

        Method getColumn = new Method();
        getColumn.setReturnType(new FullyQualifiedJavaType("String"));
        getColumn.setName("getColumn");
        getColumn.setVisibility(JavaVisibility.PRIVATE);
        getColumn.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "name"));
        getColumn.addBodyLine("Column[] values = " + domainObjectName + ".Column.values();");
        getColumn.addBodyLine("for (" + domainObjectName + ".Column value : values) {");
        getColumn.addBodyLine("if (Objects.equals(value.getJavaProperty(), name)) {");
        getColumn.addBodyLine("return value.getEscapedColumnName();");
        getColumn.addBodyLine("}");
        getColumn.addBodyLine("}");
        getColumn.addBodyLine("throw new RuntimeException(name + \"属性名不合法\");");
        topLevelClass.addMethod(getColumn);


        Method page = new Method();
        page.addAnnotation("@Override");
        page.setReturnType(topLevelClass.getType());
        page.setName("page");
        page.setVisibility(JavaVisibility.PUBLIC);
        page.addParameter(new Parameter(new FullyQualifiedJavaType("Paging"), "paging"));
        page.addBodyLine("if (!Objects.isNull(paging)) {");
        page.addBodyLine("PageRequest pageRequest = PagingUtils.of(paging);");
        page.addBodyLine("List<Sort> sorts = pageRequest.getSorts();");
        page.addBodyLine("if (Lists.iterable(sorts)) {");
        page.addBodyLine("String orders = sorts.stream().map(e -> this.getColumn(e.getName()) + \" \" + e.getDirection()).collect(Collectors.joining(\",\"));");
        page.addBodyLine(" this.setOrderByClause(orders);");
        page.addBodyLine("}");
        page.addBodyLine("this.page(pageRequest.getPage(), pageRequest.getSize());");
        page.addBodyLine("}");
        page.addBodyLine("return this;");

        topLevelClass.addMethod(page);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }


}
