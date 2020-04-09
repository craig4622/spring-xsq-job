package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Component;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

/**
 * @author xsq
 * <p>
 * <p>
 * 代理对象工厂：生成代理对象的
 */
@Component
public class ProxyFactory {

    @Autowired("transactionManager")
    private TransactionManager transactionManager;


    /**
     * Jdk动态代理
     *
     * @param obj 委托对象
     * @return 代理对象
     */
    public Object getJdkProxy(Object obj) {
        // 获取代理对象
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return transaction(obj, method, args);
                    }
                });
    }


    /**
     * 使用cglib动态代理生成代理对象
     *
     * @param obj 委托对象
     * @return
     */
    public Object getCglibProxy(Object obj) {
        return Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                return transaction(obj, method, args);
            }
        });
    }

    /**
     * 执行事务操作
     *
     * @param obj
     * @param method
     * @param args
     * @return
     */
    public Object transaction(Object obj, Method method, Object[] args) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        Object result = null;
        try {
            if (obj.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(Transactional.class) == null) {
                return method.invoke(obj, args);
            }
            // 开启事务(关闭事务的自动提交)
            this.transactionManager.beginTransaction();
            result = method.invoke(obj, args);
            // 提交事务
            this.transactionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            try {
                transactionManager.rollback();

            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
        return result;
    }
}
