package CGLIBProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyExample implements MethodInterceptor {
    /**
     * 生成CGLIB代理对象
     *
     * @param cls Class类
     * @return 代理对象
     */
    public Object getProxy(Class cls) {
        //CGLIB enhancer增强对象
        Enhancer enhancer = new Enhancer();
        //设置增强类型
        enhancer.setSuperclass(cls);
        //定义代理逻辑对象为当前对象
        enhancer.setCallback(this);
        //生成并返回代理对象
        return enhancer.create();
    }

    /**
     * 代理逻辑方法
     *
     * @param o           代理对象
     * @param method      方法
     * @param objects     方法参数
     * @param methodProxy 方法代理
     * @return 代理逻辑返回
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.err.println("调用真实对象前");
        //CGLIB反射调用真实对象方法
        Object result = methodProxy.invokeSuper(o, objects);
        System.err.println("调用真实对象后");
        return result;
    }
}
