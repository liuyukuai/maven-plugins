package com.itxiaoer.plugins.mybatis;

import com.itxiaoer.commons.core.util.Lists;
import com.itxiaoer.plugins.mybatis.coder.ControllerCoder;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;
import java.util.Properties;

/**
 * 代码生成插件
 *
 * @author : liuyk
 */
@SuppressWarnings("unused")
public class CodePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 生成额外的java类
     *
     * @param introspectedTable 表数据
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        // 所有的java源文件
        List<GeneratedJavaFile> javaFiles = super.contextGenerateAdditionalJavaFiles(introspectedTable);
        // 如果没有转为空数组
        javaFiles = Lists.empty(javaFiles);
        // 所有配置项
        Properties properties = getProperties();
        // 实体类的包名
        String modelTargetPackage = getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        GeneratedJavaFile controller = new ControllerCoder().generator(getContext(), introspectedTable, properties, modelTargetPackage);
        javaFiles.add(controller);
        return javaFiles;
    }
}
