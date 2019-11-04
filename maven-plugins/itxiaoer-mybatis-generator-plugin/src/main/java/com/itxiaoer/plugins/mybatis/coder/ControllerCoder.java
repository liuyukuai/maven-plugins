package com.itxiaoer.plugins.mybatis.coder;

import com.itxiaoer.commons.core.date.LocalDateTimeUtil;
import com.itxiaoer.plugins.mybatis.util.ControllerUtil;
import com.itxiaoer.plugins.mybatis.util.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * controller 生成器
 *
 * @author : liuyk
 */
@Slf4j
public class ControllerCoder implements Coder {

    @Override
    public GeneratedJavaFile generator(Context context, IntrospectedTable introspectedTable, Properties properties, String entityPackage) {

        // 实体类的类名
        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        // 实体类的全限定类名
        String modalFullName = entityPackage + "." + domainObjectName;
        // 采用默认值
        String webPackage = ControllerUtil.getPackage(properties, entityPackage);
        System.out.println(webPackage);
        // 创建目录
        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
        try {
            File serviceDirectory = shellCallback.getDirectory("src/main/java", webPackage);
            String absolutePath = serviceDirectory.getAbsolutePath();
            if (StringUtility.stringHasValue(absolutePath)) {
                log.info("create controller package success package=[{}]", absolutePath);
            }
        } catch (ShellException e) {
            log.error(e.getMessage(), e);
        }
        // 创建类文件
        TopLevelClass topLevelClass = new TopLevelClass(webPackage + "." + domainObjectName + "Controller");
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // 添加注解
        topLevelClass.addAnnotation("@RestController");

        // import
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RestController"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("net.tsingyun.commons.core.page.Response"));


        // 添加注释
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine("*");
        topLevelClass.addJavaDocLine("* @author : liuyk");
        topLevelClass.addJavaDocLine("* @description: " + ControllerUtil.getName(properties, domainObjectName));
        topLevelClass.addJavaDocLine("* @className: " + ControllerUtil.getName(properties, domainObjectName));
        topLevelClass.addJavaDocLine("* @date : " + LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd hh:mm:ss "));
        topLevelClass.addJavaDocLine("* @company : 清云智通（北京）科技有限公司");
        topLevelClass.addJavaDocLine("*");
        topLevelClass.addJavaDocLine("**/");

        // 添加 service 引用
        this.setField(topLevelClass, properties, ServiceUtil.getPackage(properties, entityPackage), ServiceUtil.getName(properties, domainObjectName));
        this.setMethod(topLevelClass, introspectedTable);

        return new GeneratedJavaFile(topLevelClass, "src/main/java", context.getJavaFormatter());

    }


    private void setField(TopLevelClass topLevelClass, Properties properties, String servicePackage, String serviceName) {
        Field serviceField = new Field();
        serviceField.setName(toLowerCaseFirstOne(serviceName));
        serviceField.setVisibility(JavaVisibility.PRIVATE);
        serviceField.setType(new FullyQualifiedJavaType(serviceName));
        serviceField.addAnnotation("@Resource");
        topLevelClass.addField(serviceField);
        topLevelClass.addImportedType(new FullyQualifiedJavaType(servicePackage + "." + serviceName));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));


    }


    private void setMethod(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Response<Long>"));
        method.setName("create");

        // 添加方法参数
        StringBuilder pathVar = new StringBuilder();
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
            importedTypes.add(type);
            Parameter parameter = new Parameter(type, introspectedColumn.getJavaProperty());
            parameter.addAnnotation("@PathVariable(\"" + parameter.getName() + "\")");
            pathVar.append("{");
            pathVar.append(parameter.getName());
            pathVar.append("}");
            pathVar.append("/");
            method.addParameter(parameter);
        }

        pathVar.delete(pathVar.lastIndexOf("/"), pathVar.length());
        // 添加方法注解
        method.addAnnotation("@RequestMapping(value = \"/" + pathVar.toString() + "\", method = RequestMethod.DELETE, produces = {\"application/json;charset=UTF-8\"})");

        // addBodyline,必须配置bodyline,方法才有实现体,否则这个方法就是个abstract方法了
        List<Parameter> parameters = method.getParameters();
        StringBuilder sb = new StringBuilder();
        for (Parameter p : parameters) {
            sb.append(p.getName());
            sb.append(",");
        }
        sb.delete(sb.lastIndexOf(","), sb.length());
        method.addBodyLine("int resultCount = this." + toLowerCaseFirstOne(domainObjectName) + "Service." + introspectedTable.getDeleteByPrimaryKeyStatementId() + "(" + sb.toString() + ");");
        method.addBodyLine("return new ResponseEntity<Integer>(resultCount,HttpStatus.OK);");
        topLevelClass.addMethod(method);
    }

}
