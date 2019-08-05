package Observe;

public class test {
    public static void main(String args[]){
        ProductList observable = ProductList.getInstance();
        TBObserver tbObserver = new TBObserver();
        JDObserver jdObserver = new JDObserver();
        observable.addObserver(tbObserver);
        observable.addObserver(jdObserver);
        observable.addProduct("新增产品1");
    }
}
