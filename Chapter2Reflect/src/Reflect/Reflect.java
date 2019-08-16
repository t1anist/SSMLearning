package Reflect;

import java.lang.reflect.Method;

public class Reflect {
    public static void main(String args[]){
        Reflect reflect = new Reflect();
        reflect.reflect();
    }
    public Object reflect(){
        Object returnObj = null;
        try{
            ReflectImpl target = (ReflectImpl)Class.forName("Reflect.ReflectImpl").newInstance();
            Method method = target.getClass().getMethod("sayHelloTwice", String.class, String.class);
            returnObj = method.invoke(target,"张1","张2");
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnObj;
    }
}
