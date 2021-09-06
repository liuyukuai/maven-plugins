/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.itxiaoer.plugins.mybatis.util;

import com.itxiaoer.commons.core.util.Lists;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Objects;

/**
 * examples
 *
 * @author liuyk@tsingyun.net
 */
public class Domains {

    public static void doImprots(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("javax.validation.constraints.Size");
        topLevelClass.addImportedType("javax.validation.constraints.NotBlank");
        topLevelClass.addImportedType("javax.validation.constraints.Digits");
        topLevelClass.addImportedType("javax.validation.constraints.NotEmpty");
        topLevelClass.addImportedType("net.tsingyun.commons.core.Constants");
        topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");

    }


    public static void doInnerEnums(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("net.tsingyun.commons.mybatis.MysqlColumn");
        List<InnerEnum> innerEnums = topLevelClass.getInnerEnums();
        Lists.empty(innerEnums).forEach(e-> System.out.println(e.getType()));

        FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType("net.tsingyun.commons.mybatis.MysqlColumn");

        // 如果是Column增加接口
        Lists.empty(innerEnums)
                .stream()
                .filter(e -> Objects.equals(e.getType().getShortName(), "Column"))
                .findAny()
                .ifPresent(e -> {
                    System.out.println("xxxxxxxx" + e);
                    e.addSuperInterface(fullyQualifiedJavaType);
                    // 增加override
                    List<Method> methods = e.getMethods();

                    Lists.empty(methods)
                            .stream()
                            .filter(m -> Objects.equals(m.getName(), "getEscapedColumnName"))
                            .findAny()
                            .ifPresent(m -> m.addAnnotation("@Override"));

                });

    }
}
