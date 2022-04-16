/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.itxiaoer.plugins.mybatis.util;

import com.itxiaoer.commons.core.json.JsonUtil;
import com.itxiaoer.commons.core.util.Lists;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Objects;

/**
 * 元素拷贝类
 *
 * @author liuyk@tsingyun.net
 */
public class Elements {

    public static Element replace(Element element) {
        if (element instanceof TextElement) {
            String content = ((TextElement) element).getContent();
            return new TextElement(content.replace("insert", "replace"));
        }
        XmlElement xmlElement = (XmlElement) element;

        // 拷贝本身属性
        XmlElement newElement = new XmlElement(xmlElement.getName());
        // 拷贝属性
        List<Attribute> attributes = xmlElement.getAttributes();
        Lists.empty(attributes).stream().map(e -> {
            if (Objects.equals(e.getName(), "id")) {
                return new Attribute(e.getName(), replace(e.getValue()));
            }
            return new Attribute(e.getName(), e.getValue());
        }).forEach(newElement::addAttribute);


        // 拷贝子元素
        List<Element> elements = xmlElement.getElements();
        Lists.empty(elements).stream().map(Elements::replace).forEach(newElement::addElement);
        return newElement;
    }

    public static String replace(String name) {
        return name.replace("insert", "replace").replace("Insert", "Replace");
    }

    public static boolean isInsert(XmlElement xmlElement) {
        return Objects.equals(xmlElement.getName(), "insert");
    }

}
