package com.huwo.gateway.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  19:53
 */
@Configuration
public class SpringUtils implements ApplicationContextAware {

    //Spring应用上下文环境
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 获取对象
     * @return  Object 一个以所给名字注册的bean的实例 (service注解方式，自动生成以首字母小写的类名为bean name)
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 通过class获取对象
     * @param clazz
     * @param <T>
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> clazz) throws BeansException{
        return applicationContext.getBean(clazz);
    }

}
