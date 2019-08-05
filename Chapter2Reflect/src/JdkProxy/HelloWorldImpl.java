package JdkProxy;

/**
 * 真实对象类
 */

public class HelloWorldImpl implements HelloWorld{

    @Override
    public void sayHelloWorld() {
        System.out.println("Hello World");
    }
}
