package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.commons.core.util.Lists;
import com.itxiaoer.plugins.mybatis.sqlserver.SqlServers;
import com.itxiaoer.plugins.mybatis.util.Examples;
import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 添加example接口
 *
 * @author : liuyk
 */
@SuppressWarnings("all")
public class ExampleExtendPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 处理引用
        Examples.doImprots(topLevelClass);

        Examples.doConditional(topLevelClass);

        // 属性处理
        List<Field> fields = topLevelClass.getFields();

        for (Field field : fields) {
            // 属性可见性修改为private
            field.setVisibility(JavaVisibility.PRIVATE);
        }

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        String primaryKeyType = introspectedTable.getPrimaryKeyType();
        String shortName = "Long";
        if (Lists.iterable(primaryKeyColumns)) {
            shortName = primaryKeyColumns.size() > 1 ? primaryKeyType : primaryKeyColumns.get(0)
                                                                                         .getFullyQualifiedJavaType()
                                                                                         .getShortName();
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

        // 名称集合
        Map<String, String> names = Lists.empty(introspectedTable.getAllColumns())
                                         .stream()
                                         .collect(Collectors.toMap(e -> e.getJavaProperty(), e -> e.getFullyQualifiedJavaType()
                                                                                                   .getShortName()));

        if (names.containsKey("id") && Objects.equals(names.get("id"), shortName)) {
            method.setReturnType(topLevelClass.getType());
            method.setName("ids");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + names.get("id") + ">"), "ids"));
            method.addBodyLine("this.getOredCriteria().forEach(e -> e.andIdIn(ids));");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (names.containsKey("tenantId")) {
            IntrospectedColumn field = Lists.empty(introspectedTable.getAllColumns())
                                            .stream()
                                            .filter(e -> Objects.equals(e.getJavaProperty(), "tenantId"))
                                            .findAny()
                                            .orElse(null);

            String className = field.getFullyQualifiedJavaType().getShortName();

            if (Objects.nonNull(field)) {
                method = new Method();
                method.addAnnotation("@Override");
                method.setReturnType(topLevelClass.getType());
                method.setName("tenantId");
                method.setVisibility(JavaVisibility.PUBLIC);
                method.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "tenantId"));
                method.addBodyLine("if (Objects.nonNull(tenantId)) {");
                method.addBodyLine("this.getOredCriteria().forEach(e -> e.andTenantIdEqualTo((" + className + ")tenantId));");
                method.addBodyLine("}");
                method.addBodyLine("return this;");
                topLevelClass.addMethod(method);
            }
        }

        if (names.containsKey("department")) {
            // 添加orgCodes方法
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("departments");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "departments"));
            method.addBodyLine("this.getOredCriteria().forEach(e -> {");
            method.addBodyLine("if (Lists.iterable(departments)) {");
            method.addBodyLine("Condition in = Condition.in(Column.department, departments);");
            method.addBodyLine("Condition isNull = Condition.isNull(Column.department);");
            method.addBodyLine("e.or(in, isNull);");
            method.addBodyLine("}");
            method.addBodyLine("});");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (names.containsKey("orgCode")) {
            // 添加orgCodes方法
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("orgCodes");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "orgCodes"));
            method.addBodyLine("this.getOredCriteria().forEach(e -> e.andOrgCodeIn(orgCodes));");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (names.containsKey("orgId")) {
            // 添加orgCodes方法
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("orgIds");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<Long>"), "orgIds"));
            method.addBodyLine("this.getOredCriteria().forEach(e -> e.andOrgIdIn(orgIds));");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);

        }

        if (names.containsKey("department") && names.containsKey("orgCode")) {
            // 添加orgCodes方法
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("privileges");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "orgCodes"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "departments"));
            method.addBodyLine("List<Condition> conditions = new ArrayList<>(2);");
            method.addBodyLine("if (Lists.iterable(orgCodes)) {");
            method.addBodyLine("Condition in = Condition.in(Column.orgCode, orgCodes);");
            method.addBodyLine("conditions.add(in);");
            method.addBodyLine("}");
            method.addBodyLine("if (Lists.iterable(departments)) {");
            method.addBodyLine("Condition in = Condition.in(Column.department, departments);");
            method.addBodyLine("conditions.add(in);");
            method.addBodyLine("}");
            method.addBodyLine("this.getOredCriteria().forEach(e -> {");
            method.addBodyLine("e.or(conditions.toArray(new Condition[]{}));");
            method.addBodyLine("});");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (!names.containsKey("department") && names.containsKey("orgCode")) {
            // 添加orgCodes方法
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("privileges");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "orgCodes"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "departments"));
            method.addBodyLine("List<Condition> conditions = new ArrayList<>(2);");
            method.addBodyLine("if (Lists.iterable(orgCodes)) {");
            method.addBodyLine("Condition in = Condition.in(Column.orgCode, orgCodes);");
            method.addBodyLine("conditions.add(in);");
            method.addBodyLine("}");
            method.addBodyLine("this.getOredCriteria().forEach(e -> {");
            method.addBodyLine("e.or(conditions.toArray(new Condition[]{}));");
            method.addBodyLine("});");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        String targetPackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        topLevelClass.addStaticImport(targetPackage + "." + domainObjectName + ".Column");

        Method init = new Method();
        init.setName("init");
        init.addBodyLine("this.createCriteria();");
        init.setVisibility(JavaVisibility.PUBLIC);
        init.addAnnotation("@Override");
        topLevelClass.addMethod(init);

        topLevelClass.getMethods()
                     .stream()
                     .filter(Method::isConstructor)
                     .forEach(e -> e.setVisibility(JavaVisibility.PRIVATE));

        Method getColumn = new Method();
        getColumn.setReturnType(new FullyQualifiedJavaType("String"));
        getColumn.setName("getColumn");
        getColumn.addAnnotation("@Override");
        getColumn.setVisibility(JavaVisibility.PUBLIC);
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

        Method sort = new Method();
        sort.addAnnotation("@Override");
        sort.setReturnType(topLevelClass.getType());
        sort.setName("sort");
        sort.setVisibility(JavaVisibility.PUBLIC);
        sort.addParameter(new Parameter(new FullyQualifiedJavaType("Sort"), "...sorts"));
        sort.addBodyLine("if (Lists.iterable(sorts)) {");
        sort.addBodyLine("String orders =  Stream.of(sorts).map(e -> this.getColumn(e.getName()) + \" \" + e.getDirection()).collect(Collectors.joining(\",\"));");
        sort.addBodyLine(" this.setOrderByClause(orders);");
        sort.addBodyLine("}");
        sort.addBodyLine("return this;");
        topLevelClass.addMethod(sort);

        // 处理内部类
        Examples.doInnerClasses(topLevelClass, domainObjectName);

        List<Method> methods = topLevelClass.getMethods();

        boolean sqlServer = SqlServers.isSqlServer(introspectedTable);

        if (sqlServer) {
            // 增加setRows
            Method setRows = new Method();
            setRows.addAnnotation("@Override");
            setRows.setReturnType(topLevelClass.getType());
            setRows.setName("page");
            setRows.setVisibility(JavaVisibility.PUBLIC);
            setRows.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "page"));
            setRows.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "pageSize"));
            setRows.addBodyLine("this.startRows = page * pageSize;");
            setRows.addBodyLine("this.size = ( page + 1 ) * pageSize;");
            setRows.addBodyLine("return this;");
            topLevelClass.addMethod(setRows);
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }
}
