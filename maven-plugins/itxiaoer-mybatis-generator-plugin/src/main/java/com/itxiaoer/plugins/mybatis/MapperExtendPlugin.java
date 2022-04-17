package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.commons.core.util.Lists;
import com.itxiaoer.plugins.mybatis.util.Elements;
import com.itxiaoer.plugins.mybatis.util.JavaDocUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        this.doReplace(interfaze, methods);

        interfaze.addAnnotation("@SuppressWarnings(\"ALL\")");
        // domain name
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        // key
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        String primaryKeyType = introspectedTable.getPrimaryKeyType();

        String shortName = "Long";
        if (Lists.iterable(primaryKeyColumns)) {
            shortName = primaryKeyColumns.size() > 1 ? primaryKeyType : primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName();
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

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        ReplaceMapperGenerator elementGenerator = new ReplaceMapperGenerator();
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(document.getRootElement());
        this.doIgnore(document);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void doReplace(Interface interfaze, List<Method> methodList) {
        List<Method> methods = new ArrayList<>();
        Lists.empty(methodList).forEach(method -> {
            if (method.getName().contains("insert") || method.getName().contains("Insert")) {
                Method m = new Method(method);
                m.setName(Elements.replace(method.getName()));
                methods.add(m);
            }
        });
        Lists.empty(methods).forEach(interfaze::addMethod);
    }

    private void doReplace(Document document) {
        XmlElement rootElement = document.getRootElement();
        List<Element> elements = Lists.empty(rootElement.getElements())
                                      .stream()
                                      .map(e -> (XmlElement) e)
                                      .filter(Elements::isInsert)
                                      .map(Elements::replace)
                                      .collect(Collectors.toList());
        System.out.println("##################################");
        System.out.println(elements.size());
        Lists.empty(elements).forEach(rootElement::addElement);
    }

    private void doIgnore(Document document) {
        String property = this.properties.getProperty("ignore", "false");
        if (StringUtils.isNotBlank(property) && Boolean.parseBoolean(property)) {
            XmlElement rootElement = document.getRootElement();
            rootElement.getElements()
                       .stream()
                       .map(e -> (XmlElement) e)
                       .filter(x -> Objects.equals("insert", x.getName()))
                       .map(XmlElement::getElements)
                       .flatMap(List::stream)
                       .forEach(x -> {
                           if (x instanceof TextElement) {
                               TextElement text = (TextElement) x;
                               String content = text.getContent();
                               if (content.startsWith("insert into")) {
                                   String replace = content.replace("insert into", "insert ignore into");
                                   Field field = null;
                                   try {
                                       field = TextElement.class.getDeclaredField("content");
                                       field.setAccessible(true);
                                       field.set(text, replace);
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                           }
                       });
        }
    }
}

