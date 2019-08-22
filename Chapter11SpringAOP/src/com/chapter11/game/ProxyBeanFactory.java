package com.chapter11.game;

import com.chapter11.game.interceptor.Interceptor;


public class ProxyBeanFactory {
    public static <T> T getBean(T obj, Interceptor interceptor){
        return (T) ProxyBeanUtil.getBean(obj,interceptor);
    }
}
