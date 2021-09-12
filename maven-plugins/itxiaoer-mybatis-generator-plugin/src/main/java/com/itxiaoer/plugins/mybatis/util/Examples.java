/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.itxiaoer.plugins.mybatis.util;

import com.itxiaoer.commons.core.util.Lists;
import org.mybatis.generator.api.dom.java.*;

import java.util.Objects;

/**
 * examples
 *
 * @author liuyk@tsingyun.net
 */
public class Examples {

    public static void doImprots(TopLevelClass topLevelClass) {
        //添加分页方法
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("java.util.*");
        topLevelClass.addImportedType("java.util.stream.Stream");
        topLevelClass.addImportedType("java.util.stream.Collectors");
        topLevelClass.addImportedType("org.apache.commons.text.StringEscapeUtils");
        topLevelClass.addImportedType("org.apache.commons.lang3.StringUtils");
        topLevelClass.addImportedType("net.tsingyun.commons.core.page.Paging");
        topLevelClass.addImportedType("net.tsingyun.commons.core.page.Sort");
        topLevelClass.addImportedType("net.tsingyun.commons.core.util.Lists");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.util.PagingUtils");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.PageRequest");
        topLevelClass.addImportedType("net.tsingyun.commons.core.util.Strings");

        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.Condition");
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.Criterion");
        topLevelClass.addImportedType("net.tsingyun.commons.expression.libs.Operator");
        topLevelClass.addImportedType("net.tsingyun.commons.expression.libs.Conditional");
        topLevelClass.addImportedType("net.tsingyun.commons.expression.libs.Conditionals");
    }

    public static void doConditional(TopLevelClass topLevelClass) {
        Method conditional = new Method();
        conditional.addAnnotation("@Override");
        conditional.setName("condition");
        conditional.setReturnType(topLevelClass.getType());
        conditional.setVisibility(JavaVisibility.PUBLIC);
        conditional.addParameter(new Parameter(new FullyQualifiedJavaType("Conditional"), "conditional"));
        conditional.addBodyLine("Condition condition = this.init(conditional);");
        conditional.addBodyLine("if (Objects.nonNull(condition)) {");
        conditional.addBodyLine("this.getOredCriteria().forEach(e -> e.and(condition));");
        conditional.addBodyLine("}");
        conditional.addBodyLine("return this;");
        topLevelClass.addMethod(conditional);

        Method conditionals = new Method();
        conditionals.addAnnotation("@Override");
        conditionals.setReturnType(topLevelClass.getType());
        conditionals.setName("condition");
        conditionals.setVisibility(JavaVisibility.PUBLIC);
        conditionals.addParameter(new Parameter(new FullyQualifiedJavaType("Conditionals"), "conditionals"));
        conditionals.addBodyLine("List<Condition> init = this.init(conditionals);");
        conditionals.addBodyLine("if (Lists.iterable(init)) {");
        conditionals.addBodyLine("Condition[] conditions = init.toArray(new Condition[]{});");
        conditionals.addBodyLine("Operator operator = conditionals.getOperator();");
        conditionals.addBodyLine("switch (operator) {");
        conditionals.addBodyLine("case AND:");
        conditionals.addBodyLine("this.getOredCriteria().forEach(e -> e.and(conditions));");
        conditionals.addBodyLine("return this;");
        conditionals.addBodyLine("case OR:");
        conditionals.addBodyLine("this.getOredCriteria().forEach(e -> e.or(conditions));");
        conditionals.addBodyLine("return this;");
        conditionals.addBodyLine("default:");
        conditionals.addBodyLine("return this;");
        conditionals.addBodyLine("}");
        conditionals.addBodyLine("}");
        conditionals.addBodyLine("return this;");
        topLevelClass.addMethod(conditionals);

        Method condition = new Method();
        condition.setVisibility(JavaVisibility.PUBLIC);
        condition.setReturnType(topLevelClass.getType());
        condition.addAnnotation("@Override");
        condition.setName("condition");
        condition.addParameter(new Parameter(new FullyQualifiedJavaType("Condition"), "condition"));
        condition.addBodyLine("if (Objects.nonNull(condition)) {");
        condition.addBodyLine("   this.getOredCriteria().forEach(e -> e.and(condition));");
        condition.addBodyLine("}");
        condition.addBodyLine("return this;");
        topLevelClass.addMethod(condition);

        Method conditions = new Method();
        conditions.setReturnType(topLevelClass.getType());
        conditions.setVisibility(JavaVisibility.PUBLIC);
        conditions.setName("condition");
        conditions.addAnnotation("@Override");
        conditions.addParameter(new Parameter(new FullyQualifiedJavaType("List<Condition>"), "conditions"));
        conditions.addBodyLine("return this.condition(conditions,Operator.AND);");
        topLevelClass.addMethod(conditions);

        Method conditionsOperator = new Method();
        conditionsOperator.setVisibility(JavaVisibility.PUBLIC);
        conditionsOperator.setName("condition");
        conditionsOperator.setReturnType(topLevelClass.getType());
        conditionsOperator.addAnnotation("@Override");
        conditionsOperator.addParameter(new Parameter(new FullyQualifiedJavaType("List<Condition>"), "conditions"));
        conditionsOperator.addParameter(new Parameter(new FullyQualifiedJavaType("Operator"), "operator"));
        conditionsOperator.addBodyLine("if (Lists.iterable(conditions)) {");
        conditionsOperator.addBodyLine("switch (operator) {");
        conditionsOperator.addBodyLine("case AND:");
        conditionsOperator.addBodyLine("this.getOredCriteria().forEach(e -> e.and(conditions.toArray(new Condition[]{})));");
        conditionsOperator.addBodyLine("return this;");
        conditionsOperator.addBodyLine("case OR:");
        conditionsOperator.addBodyLine("this.getOredCriteria().forEach(e -> e.or(conditions.toArray(new Condition[]{})));");
        conditionsOperator.addBodyLine("return this;");
        conditionsOperator.addBodyLine("default:");
        conditionsOperator.addBodyLine("return this;");
        conditionsOperator.addBodyLine("}");
        conditionsOperator.addBodyLine("}");
        conditionsOperator.addBodyLine("return this;");
        topLevelClass.addMethod(conditionsOperator);

    }


    public static void doInnerClasses(TopLevelClass topLevelClass, String domainObjectName) {

        // 移除一个类Criterion
        Lists.empty(topLevelClass.getInnerClasses())
                .removeIf(e -> Objects.equals(e.getType().getShortName(), "Criterion"));

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

                    // 设置方法
                    Method and = new Method();
                    and.setReturnType(new FullyQualifiedJavaType("Criteria"));
                    and.setVisibility(JavaVisibility.PUBLIC);
                    and.setName("and");
                    and.addParameter(new Parameter(new FullyQualifiedJavaType("Condition..."), "conditions"));
                    and.addBodyLine("if (Objects.isNull(conditions) || conditions.length == 0) {");
                    and.addBodyLine("return (Criteria) this;");
                    and.addBodyLine("}");
                    and.addBodyLine("List<String> sql = new ArrayList<>();");
                    and.addBodyLine("for (Condition condition : conditions) {");
                    and.addBodyLine("sql.add(condition.getSql());");
                    and.addBodyLine("}");
                    and.addBodyLine("addCriterion(\"(\" + StringUtils.join(sql, \" and \") + \")\");");
                    and.addBodyLine("return (Criteria) this;");
                    e.addMethod(and);
                });
    }

}
