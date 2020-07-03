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

        // 名称集合
        List<String> names = Lists.empty(introspectedTable.getAllColumns())
                .stream()
                .map(IntrospectedColumn::getJavaProperty)
                .collect(Collectors.toList());

        if (names.contains("id")) {
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("ids");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + shortName + ">"), "ids"));
            method.addBodyLine("this.getOredCriteria().forEach(e -> e.andIdIn(ids));");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (names.contains("tenantId")) {
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("tenantId");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "id"));
            method.addBodyLine("if (Objects.nonNull(id)) {");
            method.addBodyLine("Long tenantId = Long.valueOf(id.toString());");
            method.addBodyLine("this.getOredCriteria().forEach(e -> e.andTenantIdEqualTo(tenantId));");
            method.addBodyLine("}");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (names.contains("department")) {
            // 添加orgCodes方法
            method = new Method();
            method.addAnnotation("@Override");
            method.setReturnType(topLevelClass.getType());
            method.setName("departments");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("List<String>"), "departments"));
            method.addBodyLine("this.getOredCriteria().forEach(e -> e.andDepartmentIn(departments));");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }


        if (names.contains("orgCode")) {
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

        if (names.contains("department") && names.contains("orgCode")) {
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
            method.addBodyLine("Condition in = Condition.in(Column.department, orgCodes);");
            method.addBodyLine("conditions.add(in);");
            method.addBodyLine("}");
            method.addBodyLine("this.getOredCriteria().forEach(e -> {");
            method.addBodyLine("e.or(conditions.toArray(new Condition[]{}));");
            method.addBodyLine("});");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);
        }

        if (!names.contains("department") && names.contains("orgCode")) {
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


        //添加分页方法
        topLevelClass.addImportedType("java.util.*");
        topLevelClass.addImportedType("java.util.stream.Collectors");
        topLevelClass.addImportedType("net.tsingyun.commons.core.page.Paging");
        topLevelClass.addImportedType("net.tsingyun.commons.core.page.Sort");
        topLevelClass.addImportedType("net.tsingyun.commons.core.util.Lists");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.util.PagingUtils");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.PageRequest");
        topLevelClass.addImportedType("org.apache.commons.text.StringEscapeUtils");
        topLevelClass.addImportedType("org.apache.commons.lang3.StringUtils");
        topLevelClass.addImportedType("net.tsingyun.commons.core.util.Strings");
        topLevelClass.addImportedType("lombok.Data");

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

        topLevelClass.addInnerClass(createCondition(domainObjectName));


        topLevelClass.getInnerClasses()
                .stream()
                .filter(e -> Objects.equals("GeneratedCriteria", e.getType().getShortName()))
                .findAny()
                .ifPresent(e -> {
                    // 设置方法
                    Method orLike = new Method();
                    orLike.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    orLike.setVisibility(JavaVisibility.PUBLIC);
                    orLike.setName("orLike");
                    orLike.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
                    orLike.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column..."), "columns"));
                    orLike.addBodyLine("if (Objects.isNull(value) || StringUtils.isBlank(value.toString()) || Objects.isNull(columns)) {");
                    orLike.addBodyLine(" return (Criteria) this;");
                    orLike.addBodyLine("}");
                    orLike.addBodyLine("List<String> sql = new ArrayList<>();");
                    orLike.addBodyLine("for (Column column : columns) {");
                    orLike.addBodyLine(" sql.add(column.getEscapedColumnName() + \" like '%\" + StringEscapeUtils.escapeXSI(value.toString()) + \"%'\");");
                    orLike.addBodyLine("}");
                    orLike.addBodyLine("addCriterion(\"(\" + StringUtils.join(sql, \" or \") + \")\");");
                    orLike.addBodyLine("return (Criteria) this;");
                    e.addMethod(orLike);

                    // 设置方法
                    Method orRightLike = new Method();
                    orRightLike.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    orRightLike.setVisibility(JavaVisibility.PUBLIC);
                    orRightLike.setName("orRightLike");
                    orRightLike.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
                    orRightLike.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column..."), "columns"));
                    orRightLike.addBodyLine("if (Objects.isNull(value) || StringUtils.isBlank(value.toString()) || Objects.isNull(columns)) {");
                    orRightLike.addBodyLine(" return (Criteria) this;");
                    orRightLike.addBodyLine("}");
                    orRightLike.addBodyLine("List<String> sql = new ArrayList<>();");
                    orRightLike.addBodyLine("for (Column column : columns) {");
                    orRightLike.addBodyLine(" sql.add(column.getEscapedColumnName() + \" like '\" + StringEscapeUtils.escapeXSI(value.toString()) + \"%'\");");
                    orRightLike.addBodyLine("}");
                    orRightLike.addBodyLine("addCriterion(\"(\" + StringUtils.join(sql, \" or \") + \")\");");
                    orRightLike.addBodyLine("return (Criteria) this;");
                    e.addMethod(orRightLike);

                    // 设置方法
                    Method orLeftLike = new Method();
                    orLeftLike.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    orLeftLike.setVisibility(JavaVisibility.PUBLIC);
                    orLeftLike.setName("orLeftLike");
                    orLeftLike.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
                    orLeftLike.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column..."), "columns"));
                    orLeftLike.addBodyLine("if (Objects.isNull(value) || StringUtils.isBlank(value.toString()) || Objects.isNull(columns)) {");
                    orLeftLike.addBodyLine(" return (Criteria) this;");
                    orLeftLike.addBodyLine("}");
                    orLeftLike.addBodyLine("List<String> sql = new ArrayList<>();");
                    orLeftLike.addBodyLine("for (Column column : columns) {");
                    orLeftLike.addBodyLine(" sql.add(column.getEscapedColumnName() + \" like '%\" + StringEscapeUtils.escapeXSI(value.toString()) + \"'\");");
                    orLeftLike.addBodyLine("}");
                    orLeftLike.addBodyLine("addCriterion(\"(\" + StringUtils.join(sql, \" or \") + \")\");");
                    orLeftLike.addBodyLine("return (Criteria) this;");
                    e.addMethod(orLeftLike);


                    // 设置方法
                    Method orEq = new Method();
                    orEq.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    orEq.setVisibility(JavaVisibility.PUBLIC);
                    orEq.setName("orEq");
                    orEq.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
                    orEq.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column..."), "columns"));
                    orEq.addBodyLine("if (Objects.isNull(value) || StringUtils.isBlank(value.toString()) || Objects.isNull(columns)) {");
                    orEq.addBodyLine(" return (Criteria) this;");
                    orEq.addBodyLine("}");
                    orEq.addBodyLine("List<String> sql = new ArrayList<>();");
                    orEq.addBodyLine("for (Column column : columns) {");
                    orEq.addBodyLine(" sql.add(column.getEscapedColumnName() + \" = '\" + value+\"'\");");
                    orEq.addBodyLine("}");
                    orEq.addBodyLine("addCriterion(\"(\" + StringUtils.join(sql, \" or \") + \")\");");
                    orEq.addBodyLine("return (Criteria) this;");
                    e.addMethod(orEq);

                    // 设置方法
                    Method or = new Method();
                    or.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    or.setVisibility(JavaVisibility.PUBLIC);
                    or.setName("or");
                    or.addParameter(new Parameter(new FullyQualifiedJavaType("Condition..."), "conditions"));
                    or.addBodyLine("if (Objects.isNull(conditions) || conditions.length == 0) {");
                    or.addBodyLine("return (Criteria) this;");
                    or.addBodyLine("}");
                    or.addBodyLine("List<String> sql = new ArrayList<>();");
                    or.addBodyLine("for (Condition condition : conditions) {");
                    or.addBodyLine("sql.add(condition.getSql());");
                    or.addBodyLine("}");
                    or.addBodyLine("addCriterion(\"(\" + StringUtils.join(sql, \" or \") + \")\");");
                    or.addBodyLine("return (Criteria) this;");
                    e.addMethod(or);
                });

        topLevelClass.addMethod(page);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }


    public InnerClass createCondition(String domainObjectName) {
        // 添加内部类
        InnerClass innerClass = new InnerClass("Condition");
        innerClass.setStatic(true);
        innerClass.setVisibility(JavaVisibility.PUBLIC);

        Field sql = new Field();
        sql.setName("sql");
        sql.setType(new FullyQualifiedJavaType("String"));
        sql.setVisibility(JavaVisibility.PRIVATE);
        innerClass.addField(sql);

        innerClass.addAnnotation("@Data");


        Method eq = new Method();
        eq.setReturnType(new FullyQualifiedJavaType("Condition"));
        eq.setVisibility(JavaVisibility.PUBLIC);
        eq.setStatic(true);
        eq.setName("eq");
        eq.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        eq.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
        eq.addBodyLine("String sql = column.getEscapedColumnName() + \" ='\" + value + \"'\";");
        eq.addBodyLine("return init(sql);");
        innerClass.addMethod(eq);

        Method isNull = new Method();
        isNull.setReturnType(new FullyQualifiedJavaType("Condition"));
        isNull.setVisibility(JavaVisibility.PUBLIC);
        isNull.setStatic(true);
        isNull.setName("isNull");
        isNull.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        isNull.addBodyLine("String sql = column.getEscapedColumnName() + \" is null \";");
        isNull.addBodyLine("return init(sql);");
        innerClass.addMethod(isNull);


        Method isNotNull = new Method();
        isNotNull.setReturnType(new FullyQualifiedJavaType("Condition"));
        isNotNull.setVisibility(JavaVisibility.PUBLIC);
        isNotNull.setStatic(true);
        isNotNull.setName("isNotNull");
        isNotNull.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        isNotNull.addBodyLine("String sql = column.getEscapedColumnName() + \" is not null \";");
        isNotNull.addBodyLine("return init(sql);");
        innerClass.addMethod(isNotNull);


        Method notEq = new Method();
        notEq.setReturnType(new FullyQualifiedJavaType("Condition"));
        notEq.setVisibility(JavaVisibility.PUBLIC);
        notEq.setStatic(true);
        notEq.setName("notEq");
        notEq.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        notEq.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
        notEq.addBodyLine("String sql = column.getEscapedColumnName() + \" <>'\" + value + \"'\";");
        notEq.addBodyLine("return init(sql);");
        innerClass.addMethod(notEq);


        Method notIn = new Method();
        notIn.setReturnType(new FullyQualifiedJavaType("Condition"));
        notIn.setVisibility(JavaVisibility.PUBLIC);
        notIn.setStatic(true);
        notIn.setName("notIn");
        notIn.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        notIn.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
        notIn.addBodyLine("if (value instanceof Collection) {");
        notIn.addBodyLine("value = \"(\" + Strings.sql((Collection) value) + \")\";");
        notIn.addBodyLine("} else {");
        notIn.addBodyLine("value = \"(\" + Strings.sql(value) + \")\";");
        notIn.addBodyLine("}");
        notIn.addBodyLine("String sql = column.getEscapedColumnName() + \" not in \" + value + \"\";");
        notIn.addBodyLine(" return init(sql);");
        innerClass.addMethod(notIn);


        Method in = new Method();
        in.setReturnType(new FullyQualifiedJavaType("Condition"));
        in.setVisibility(JavaVisibility.PUBLIC);
        in.setStatic(true);
        in.setName("in");
        in.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        in.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
        in.addBodyLine("if (value instanceof Collection) {");
        in.addBodyLine("value = \"(\" + Strings.sql((Collection) value) + \")\";");
        in.addBodyLine("} else {");
        in.addBodyLine("value = \"(\" + Strings.sql(value) + \")\";");
        in.addBodyLine("}");
        in.addBodyLine("String sql = column.getEscapedColumnName() + \" in \" + value + \"\";");
        in.addBodyLine("return init(sql);");
        innerClass.addMethod(in);


        Method find = new Method();
        find.setReturnType(new FullyQualifiedJavaType("Condition"));
        find.setVisibility(JavaVisibility.PUBLIC);
        find.setStatic(true);
        find.setName("find");
        find.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName + ".Column"), "column"));
        find.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "value"));
        find.addBodyLine("if (value instanceof Collection) {");
        find.addBodyLine("Collection list = (Collection) value;");
        find.addBodyLine("List<String> sqls = new ArrayList<>();");
        find.addBodyLine("for (Object o : list) {");
        find.addBodyLine("sqls.add(\"FIND_IN_SET('\" + o + \"', \" + column.getEscapedColumnName() + \")\");");
        find.addBodyLine("}");
        find.addBodyLine("return init(StringUtils.join(sqls, \" or \"));");
        find.addBodyLine("}");
        find.addBodyLine("return init(\"FIND_IN_SET('\" + value + \"', \" + column.getEscapedColumnName() + \")\");");
        innerClass.addMethod(find);

        Method init = new Method();
        init.setReturnType(new FullyQualifiedJavaType("Condition"));
        init.setVisibility(JavaVisibility.PRIVATE);
        init.setStatic(true);
        init.setName("init");
        init.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "sql"));
        init.addBodyLine("Condition condition = new Condition();");
        init.addBodyLine("condition.setSql(sql);");
        init.addBodyLine("return condition;");
        innerClass.addMethod(init);

        return innerClass;

    }


}
