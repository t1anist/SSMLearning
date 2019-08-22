package com.chapter10.annotation.config;

import com.chapter10.annotation.pojo.Role;
import com.chapter10.annotation.service.impl.RoleServiceImpl;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {Role.class,RoleServiceImpl.class})
public class ApplicationConfig {
}
