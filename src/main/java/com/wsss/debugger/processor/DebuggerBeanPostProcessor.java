package com.wsss.debugger.processor;

import com.wsss.debugger.annotation.Debugger;
import com.wsss.debugger.config.DebuggerConfig;
import com.wsss.debugger.invocation.DebuggerInvocationHandler;
import com.wsss.debugger.invocation.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


/**
 * Debugger Bean处理器
 * 拦截带有@Debugger注解或在配置文件debugger.class.name中指定的类
 * 为这些类生成包含bean名称信息的动态代理
 */
@Component
@ConditionalOnProperty(name = "wsss.debugger.mode", havingValue = "local")
public class DebuggerBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DebuggerBeanPostProcessor.class);

    @Autowired
    private DebuggerConfig debuggerConfig;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
    /**
     * 检查类实现的接口上是否有Debugger注解
     * @param beanClass 要检查的类
     * @return 如果任何接口上有Debugger注解则返回true，否则返回false
     */
    private boolean hasInterfaceWithDebuggerAnnotation(Class<?> beanClass) {
        for (Class<?> intf : beanClass.getInterfaces()) {
            if (intf.isAnnotationPresent(Debugger.class)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否是FactoryBean且创建的对象类型上有Debugger注解
     * @param bean 要检查的bean
     * @return 如果是FactoryBean且创建的对象类型上有Debugger注解则返回true，否则返回false
     */
    private boolean isFactoryBeanWithDebuggerTarget(Object bean) {
        if (bean instanceof FactoryBean<?>) {
            try {
                // 获取FactoryBean创建的对象类型
                Class<?> objectType = ((FactoryBean<?>) bean).getObjectType();
                if (objectType != null) {
                    // 检查对象类型上是否有Debugger注解
                    return objectType.isAnnotationPresent(Debugger.class) || hasInterfaceWithDebuggerAnnotation(objectType);
                }
            } catch (Exception e) {
                // 如果获取对象类型失败，记录日志但不影响正常流程
                logger.debug("获取FactoryBean对象类型失败: beanName={}", bean.getClass().getName(), e);
            }
        }
        return false;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (bean instanceof FactoryBean<?>) {
            return bean;
        }

        // 检查是否需要代理：1. 有@Debugger注解 2. 接口上有@Debugger注解 3. 是工厂类且创建的对象有Debugger注解 4. 在配置列表中
        boolean needProxy = beanClass.isAnnotationPresent(Debugger.class)
                || hasInterfaceWithDebuggerAnnotation(beanClass)
                || isFactoryBeanWithDebuggerTarget(bean)
                || debuggerConfig.getClassNames().contains(beanClass)
                || debuggerConfig.getBeanNames().contains(beanName);

        if (needProxy) {
            logger.info("为Bean生成动态代理: beanName={}", beanName);
            return Proxy.getProxy(bean, new DebuggerInvocationHandler(beanName, debuggerConfig));
        }
        
        return bean;
    }
    

}