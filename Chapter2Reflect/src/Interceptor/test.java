package Interceptor;

import JdkProxy.HelloWorld;
import JdkProxy.HelloWorldImpl;

public class test {
    public static void main(String[] args){
        HelloWorld proxy = (HelloWorld) InterceptorJdkProxy.bind(new HelloWorldImpl(),"Interceptor.MyInterceptor");
        proxy.sayHelloWorld();
    }
}
