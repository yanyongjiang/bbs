package com.yyj.apps.framework.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Spring上下文，用于非注入类，获取注入的实例
 */
@Configuration
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Object lock=new Object();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }

}
