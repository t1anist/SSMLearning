package com.chapter11.aop.service.impl;

import com.chapter11.aop.service.RoleService;
import com.chapter11.game.pojo.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RoleServiceImpl implements RoleService {
    @Override
    public void printRole(Role role) {
        System.out.println("{id ="+role.getId()+", roleName ="+role.getRoleName()+", note ="+role.getNote());
    }
}
