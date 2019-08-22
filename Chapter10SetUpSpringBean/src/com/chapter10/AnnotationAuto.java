package com.chapter10;

import com.chapter10.annotation.config.ApplicationConfig;
import com.chapter10.annotation.pojo.Role;
import com.chapter10.annotation.service.RoleService2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationAuto {
    public static void main(String args[]){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        Role role = context.getBean(Role.class);
        RoleService2 roleService = context.getBean(RoleService2.class);
        roleService.printRoleInfo();
        context.close();
    }
}
