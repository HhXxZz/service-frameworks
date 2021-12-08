package org.hxz.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hxz on 2021/12/7 10:29.
 */

//@Component
public class BeanCostInitTime implements BeanPostProcessor {

    private static final Logger logger = LogManager.getLogger(BeanCostInitTime.class);
    private static final Map<String,Long> TIME_MAP = new ConcurrentHashMap<>();


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        TIME_MAP.put(beanName,System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Long startTime = TIME_MAP.get(beanName);
        if(startTime != null){
            logger.info("beanName:{} cost:{}",beanName,System.currentTimeMillis() - startTime);
        }
        return bean;
    }

}
