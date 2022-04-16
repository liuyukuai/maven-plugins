/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.commons.core.json.JsonUtil;
import com.itxiaoer.commons.core.util.Lists;
import com.itxiaoer.plugins.mybatis.util.Elements;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.ibatis2.sqlmap.elements.AbstractXmlElementGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuyk@tsingyun.net
 */
public class ReplaceMapperGenerator extends AbstractXmlElementGenerator {

    @Override
    public void addElements(XmlElement xmlElement) {
        this.doReplace(xmlElement);

    }

    private void doReplace(XmlElement root) {
        List<Element> elements = Lists.empty(root.getElements())
                                      .stream()
                                      .map(e -> (XmlElement) e)
                                      .filter(Elements::isInsert)
                                      .map(Elements::replace)
                                      .collect(Collectors.toList());
        System.out.println("##################################");
        System.out.println(elements.size());
        Lists.empty(elements).forEach(e -> {
            System.out.println(JsonUtil.toJson(e));
            root.addElement(e);
        });
    }
}
