package com.chapter11.aop;

import com.chapter11.aop.config.AopConfig;
import com.chapter11.game.pojo.Role;
import com.chapter11.aop.service.RoleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String args[]){
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AopConfig.class);
        RoleService roleService = ctx.getBean(RoleService.class);
        Role role = new Role(1L,"role_name_2","note_2");
        roleService.printRole(role);
        System.out.println("#############################");
        role=null;
        roleService.printRole(role);
    }
}
