package Observe;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ProductList extends Observable {

    private List<String> productList = null; //产品列表
    private static ProductList instance; //类唯一实例

    private ProductList(){
    }

    /**
     * 取得唯一实例
     * @return 唯一实例
     */
    public static ProductList getInstance(){
        if(instance == null){
            instance = new ProductList();
            instance.productList = new ArrayList<>();
        }
        return instance;
    }

    /**
     * 增加观察者（电商接口)
     * @param observer 观察者
     */
    public void addProductListObserver(Observer observer){
        this.addObserver(observer);
    }

    /**
     * 新增产品
     * @param newProduct 新产品
     */
    public void addProduct(String newProduct){
        productList.add(newProduct);
        System.out.println("产品列表增加了产品"+newProduct);
        this.setChanged();
        this.notifyObservers(newProduct);
    }


}
