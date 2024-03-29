/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.itxiaoer.plugins.mybatis.sqlserver;

/**
 * @author liuyk@tsingyun.net
 * @date 2022/3/10 5:03 PM
 */

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class SqlServicePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * 为每个Example类添加top属性以及set、get方法
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();

        Field top = new Field();
        top.setName("startRows");//为每页显示条数
        top.setVisibility(JavaVisibility.PRIVATE);
        top.setType(integerWrapper);
        topLevelClass.addField(top);

        Method setTop = new Method();
        setTop.setVisibility(JavaVisibility.PUBLIC);
        setTop.setName("setStartRows");
        setTop.addParameter(new Parameter(integerWrapper, "startRows"));
        setTop.addBodyLine("this.startRows = startRows;");
        topLevelClass.addMethod(setTop);

        Method getTop = new Method();
        getTop.setVisibility(JavaVisibility.PUBLIC);
        getTop.setReturnType(integerWrapper);
        getTop.setName("getStartRows");
        getTop.addBodyLine("return startRows;");
        topLevelClass.addMethod(getTop);

        Field page = new Field();
        page.setName("size");//当前页数
        page.setVisibility(JavaVisibility.PRIVATE);
        page.setType(integerWrapper);
        topLevelClass.addField(page);

        Method setPage = new Method();
        setPage.setVisibility(JavaVisibility.PUBLIC);
        setPage.setName("setSize");
        setPage.addParameter(new Parameter(integerWrapper, "size"));
        setPage.addBodyLine("this.size = size;");
        topLevelClass.addMethod(setPage);

        Method getPage = new Method();
        getPage.setVisibility(JavaVisibility.PUBLIC);
        getPage.setReturnType(integerWrapper);
        getPage.setName("getSize");
        getPage.addBodyLine("return size;");
        topLevelClass.addMethod(getPage);

        return true;
    }

    /**
     * 为Mapper.xml的selectByExample添加top
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        XmlElement ifTopNotNullElement = new XmlElement("if");
        ifTopNotNullElement.addAttribute(new Attribute("test", "startRows != null"));
        ifTopNotNullElement.addElement(new TextElement(" offset ${startRows} rows fetch next ${size} rows only "));
        element.addElement(6, ifTopNotNullElement);

//        offset 4 rows fetch next 5 rows only
//        XmlElement ifTopNotNullElement = new XmlElement("if");
//        ifTopNotNullElement.addAttribute(new Attribute("test", "startRows != null"));
//        ifTopNotNullElement.addElement(new TextElement("select * from \r\n" + "(select *, ROW_NUMBER() OVER(order by ${orderByClause}) AS RowNumber from( "));
//        element.addElement(5, ifTopNotNullElement);
//
//
//
//
//        XmlElement ifTopNotNullElement_end = new XmlElement("if");
//        ifTopNotNullElement_end.addAttribute(new Attribute("test", "startRows != null"));
//        ifTopNotNullElement_end.addElement(new TextElement(")as a)as b where RowNumber BETWEEN (${startRows} and ${endRows}"));
//        element.addElement(7, ifTopNotNullElement_end);

        return true;
    }
}
