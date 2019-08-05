package CGLIBProxy;

public class testCGLIBProxy {
    public static void main(String args[]) {
        CglibProxyExample cglibProxyExample = new CglibProxyExample();
        ReflectServieImpl reflectServie = (ReflectServieImpl) cglibProxyExample.getProxy(ReflectServieImpl.class);
        reflectServie.sayHello("张三");
    }
}
