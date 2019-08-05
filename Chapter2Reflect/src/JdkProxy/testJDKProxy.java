package JdkProxy;

public class testJDKProxy {
    public static void main(String args[]){
        JdkProxyExample jdkProxyExample = new JdkProxyExample();
        HelloWorld proxy = (HelloWorld)jdkProxyExample.bind(new HelloWorldImpl());
        proxy.sayHelloWorld();
    }
}
