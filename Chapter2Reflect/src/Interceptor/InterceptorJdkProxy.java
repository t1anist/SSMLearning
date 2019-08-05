package Interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterceptorJdkProxy implements InvocationHandler {

    private Object target; //真实对象
    private String interceptorClass = null; //拦截器限定全名

    public InterceptorJdkProxy(Object target, String interceptorClass) {
        this.target = target;
        this.interceptorClass = interceptorClass;
    }

    /**
     * 绑定委托对象并返回一个代理【占位】
     * @param target 真实对象
     * @param interceptorClass 拦截器全名
     * @return 代理对象【占位】
     */
    public static Object bind(Object target, String interceptorClass){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),new InterceptorJdkProxy(target,interceptorClass));
    }

    /**
     * 通过代理对象调用方法，首先进入这个方法
     * @param proxy 代理对象
     * @param method 被调用方法
     * @param args 方法参数
     * @return 方法返回
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (interceptorClass == null) {
            return method.invoke(target, args);
        }
        Object result = null;
        Interceptor interceptor = (Interceptor) Class.forName(interceptorClass).newInstance();
        if (interceptor.before(proxy, target, method, args)) {
            result = method.invoke(target, args);
        } else {
            interceptor.around(proxy, target, method, args);
        }
        interceptor.after(proxy, target, method, args);
        return result;
    }
}
