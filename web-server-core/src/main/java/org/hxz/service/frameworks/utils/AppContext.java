package org.hxz.service.frameworks.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by hxz on 2021/12/1 15:23.
 */

@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> var1){
        return applicationContext.getBean(var1);
    }

    public static <T> T getBean(Class<T> var1,String beanName){
        return applicationContext.getBean(beanName,var1);
    }


}
