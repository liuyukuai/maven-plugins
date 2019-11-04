package com.itxiaoer.plugins.mybatis.util;

import java.util.Properties;

/**
 * 工具类
 *
 * @author : liuyk
 */
public final class ServiceUtil {

    public static String getPackage(Properties properties, String entityPackage) {
        return entityPackage.substring(0, entityPackage.lastIndexOf(".")) + "." + "service";
    }

    public static String getName(Properties properties, String domainObjectName) {
        return domainObjectName + "Service";
    }
}
