package com.chapter10;

import com.chapter10.annotation.pojo.PojoConfig;
import com.chapter10.annotation.pojo.Role;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationMain {
    public static void main(String args[]){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(PojoConfig.class);
        Role role = applicationContext.getBean(Role.class);
        System.out.println(role.getId());
    }
}
