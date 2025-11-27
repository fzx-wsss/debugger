package com.wsss.debugger.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Debugger Spring配置类
 * 负责注册DebuggerBeanPostProcessor并启用拦截器功能
 */
@Component
public class DebuggerConfig {

    private static final Logger logger = LoggerFactory.getLogger(DebuggerConfig.class);

    @Value("${wsss.debugger.class.names:}")
    private List<String> classNames;

    @Value("${wsss.debugger.bean.names:}")
    private Set<String> beanNames;

    @Value("${wsss.debugger.proxy.enable:false}")
    private boolean enable;

    @Value("${wsss.debugger.proxy.password:}")
    private String password;

    @Value("${wsss.debugger.proxy.url:}")
    private String url;
    
    // 缓存已加载的Class对象集合
    private Set<Class> loadedClasses;

    public Set<Class> getClassNames() {
        // 检查loadedClasses是否已初始化
        if (loadedClasses == null) {
            // 创建新的加载类集合
            Set<Class> classSet = new HashSet<>(classNames.size());
            // 遍历classNames，加载对应的类到集合中
            for (String className : classNames) {
                if (className != null && !className.trim().isEmpty()) {
                    try {
                        Class<?> clazz = Class.forName(className.trim());
                        classSet.add(clazz);
                        logger.info("已加载类: {}", className.trim());
                    } catch (ClassNotFoundException e) {
                        logger.error("加载类失败: {}", className.trim(), e);
                    }
                }
            }
            logger.info("类加载完成，成功加载 {} 个类", classSet.size());
            loadedClasses = classSet;
        }

        return loadedClasses;
    }

    public Set<String> getBeanNames() {
        return beanNames;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}