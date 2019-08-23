package com.chapter11.aop.aspect;
import org.aspectj.lang.annotation.*;

@Aspect
public class RoleAspect {

    @Pointcut("execution(* com.chapter11.aop.service.impl.RoleServiceImpl.printRole(..))")
    public void print(){}

    @Before("execution(* com.chapter11.aop.service.impl.RoleServiceImpl.printRole(..))")
    public void before(){
        System.out.println("before...");
    }

    @After("execution(* com.chapter11.aop.service.impl.RoleServiceImpl.printRole(..))")
    public void after(){
        System.out.println("after...");
    }
    @AfterReturning("execution(* com.chapter11.aop.service.impl.RoleServiceImpl.printRole(..))")
    public void afterReturning(){
        System.out.println("afterReturning...");
    }
    @AfterThrowing("print()")
    public void afterThrowing(){
        System.out.println("afterThrowing...");
    }
}
