package com.lagou.edu.factory;

import com.lagou.edu.context.IOCContainer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractBeanFactory
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:55
 **/
public abstract class AbstractBeanFactory implements BeanFactory {

    protected IOCContainer iOCContainer;

    /**
     * 存储Transaction注解对应对象容器
     */
    private Map<String, Object> initTransactionMap = new ConcurrentHashMap<>(256);


    /**
     * 存储autowrite注解的对应的属性
     */
    protected Map<String, Field> txCache = new ConcurrentHashMap<>(256);

    public AbstractBeanFactory(IOCContainer iOCContainer) {
        this.iOCContainer = iOCContainer;
    }

    /**
     * 重新注入代理对象到ioc容器
     *
     * @param beanId
     * @param obj
     */
    public void packTransaction(String beanId, Object obj) {
        //把代理对象重新写入ioc容器中,这样才能使事务管理生效
        if (!initTransactionMap.containsKey(beanId)) {
            this.iOCContainer.packObject(beanId, obj);
            initTransactionMap.put(beanId, obj);
        }
    }


    /**
     * 根据id获取实例化对象
     *
     * @param id
     * @return
     */
    @Override
    public Object getBean(String id) {
        return iOCContainer.getSingleObject().get(id);
    }

    /**
     * 初始化对象实例
     */
    public abstract void buildInstance();

    /**
     * 进行实例注入
     */
    public abstract void injectionProperty();

    /**
     * 进行事务处理
     */
    public abstract void initTransaction();

    /**
     * 真正去初始化对象并保存到ioc容器中
     *
     * @param beanId
     * @param aClass
     */
    public void doCreateInstance(String beanId, Class<?> aClass) {
        Object o = null;
        try {
            o = aClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.iOCContainer.packObject(beanId, o);
    }


}
