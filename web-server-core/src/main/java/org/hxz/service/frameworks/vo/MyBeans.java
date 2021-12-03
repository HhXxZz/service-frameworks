package org.hxz.service.frameworks.vo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by hxz on 2021/11/26 17:53.
 */

@Component
public class MyBeans {
    private String name;

    public MyBeans() {
    }

    public MyBeans(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyBeans{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    @Bean(name = "bean1")
    public MyBeans customerAccount(){
        return new MyBeans("Test User 1");
    }

    @Bean(name = "bean2")
    public MyBeans customerAccount1(){
        return new MyBeans("Test User 2");
    }



}
