package com.chapter10;

import com.chapter10.annotation.config.ApplicationConfig;
import com.chapter10.annotation.pojo.Role;
import com.chapter10.annotation.service.RoleService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationPacages {
    public static void main(String args[]){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        Role role = context.getBean(Role.class);
        RoleService roleService = context.getBean(RoleService.class);
        roleService.printRoleInfo(role);
        context.close();
    }
}
