package com.lagou.edu.factory;

import com.lagou.edu.annotation.*;
import com.lagou.edu.context.IOCContainer;
import com.lagou.edu.utils.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;

import javax.servlet.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @ClassName AnnotationBeanFactory
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:39
 **/
public class AnnotationBeanFactory extends AbstractBeanFactory {

    private Reflections reflections;

    private ServletContextEvent servletContextEvent;

    private final static String BACKPACKS = "com.lagou.edu";

    private final static String PROXYFACTORY = "proxyFactory";


    public AnnotationBeanFactory(IOCContainer iOCContainer, ServletContextEvent servletContextEvent) {
        super(iOCContainer);
        this.servletContextEvent = servletContextEvent;
    }

    /**
     * 初始化对象实例
     */
    @Override
    public void buildInstance() {
        reflections = new Reflections(BACKPACKS);
        //扫描包含Component
        Set<Class<?>> componentClassesList = reflections.getTypesAnnotatedWith(Component.class);
        //扫描Service注解接口
        Set<Class<?>> serviceClassesList = reflections.getTypesAnnotatedWith(Service.class);
        //扫描Service注解接口
        Set<Class<?>> controllerClassesList = reflections.getTypesAnnotatedWith(Controller.class);
        this.buildComponent(componentClassesList);

        this.buildService(serviceClassesList);

        this.buildController(controllerClassesList);

    }

    /**
     * 把自定义的Controller加入到servlet管理中,并对类进行初始化加入到ioc容器中
     *
     * @param controllerClassesList
     */
    private void buildController(Set<Class<?>> controllerClassesList) {
        controllerClassesList.forEach(aClass -> {
            //获取该注解详细信息
            Controller controller = aClass.getAnnotation(Controller.class);
            String beanId = controller.value();
            String url = controller.url();
            Class<? extends Servlet> servletClass = (Class<? extends Servlet>) aClass;
            try {
                //根据自定义注解对应的class创建servlet
                Servlet servlet = servletContextEvent.getServletContext().createServlet(servletClass);
                //把自定义的servlet加入tomcat容器管理
                ServletRegistration.Dynamic dynamic = servletContextEvent.getServletContext().addServlet(beanId, servlet);
                //添加访问路径
                dynamic.addMapping(url);
                //对应bean加入自定义的ioc容器中
                this.iOCContainer.packObject(beanId, servlet);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 构建Component 实例初始化并放入ioc对象容器中
     *
     * @param componentClassesList
     */
    private void buildComponent(Set<Class<?>> componentClassesList) {
        componentClassesList.forEach(aClass ->
                {
                    Component annotation = aClass.getAnnotation(Component.class);
                    String beanId = annotation.value();
                    createInstance(beanId, aClass);
                }
        );
    }


    /**
     * 构建Service 实例初始化并放入ioc对象容器中
     *
     * @param serviceClassesList
     */
    private void buildService(Set<Class<?>> serviceClassesList) {
        serviceClassesList.forEach(aClass ->
                {
                    Service annotation = aClass.getAnnotation(Service.class);
                    String beanId = annotation.value();
                    createInstance(beanId, aClass);
                }
        );
    }

    /**
     * 给对象注入属性
     */
    @Override
    public void injectionProperty() {
        reflections = new Reflections(BACKPACKS, new FieldAnnotationsScanner());
        Set<Field> fields = reflections.getFieldsAnnotatedWith(Autowired.class);
        fields.forEach(field -> {
            Autowired annotation = field.getAnnotation(Autowired.class);
            //获取该注解所在的类的class
            Class<?> declaringClass = field.getDeclaringClass();
            String simpleName = StringUtils.toLowerCaseFirstOne(declaringClass.getSimpleName());
            Object o = this.getBean(simpleName);
            if (o == null) {
                throw new RuntimeException("该对象没有进行初始化");
            }
            String beanId = annotation.value();
            if (beanId.isEmpty()) {
                beanId = field.getName();
            }
            Object property = this.getBean(beanId);
            //给@Autowired注解所在的对象通过反射设置@Autowired注解的对应的属性
            field.setAccessible(true);
            try {
                field.set(o, property);
                //Autowired的名称和该注解所在的类对象,在事务处理重新塞入代理对象的时候需要
                this.txCache.put(beanId, field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 进行事务处理
     */
    @Override
    public void initTransaction() {
        reflections = new Reflections(BACKPACKS, new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Transactional.class);
        methods.forEach(method -> {
            Class<?> declaringClass = method.getDeclaringClass();
            String beanId = StringUtils.toLowerCaseFirstOne(declaringClass.getSimpleName());
            Object obj = this.getBean(beanId);
            if (obj == null) {
                throw new RuntimeException("initTransaction:对象没有初始化");
            }
            Class<?>[] interfaces = declaringClass.getInterfaces();
            //获取代理工厂实体类
            ProxyFactory proxyFactory = (ProxyFactory) this.getBean(PROXYFACTORY);
            Object proxyBean = null;
            //如果该类实现了接口则用jdk代理否则用cglib代理
            if (interfaces != null && interfaces.length > 0) {
                proxyBean = proxyFactory.getJdkProxy(obj);
                //把代理对象重新写入ioc容器中,这样才能使事务管理生效
                this.packTransaction(beanId, proxyBean);
            } else {
                proxyBean = proxyFactory.getCglibProxy(obj);
                this.packTransaction(beanId, proxyBean);
            }
            txCache.forEach((beanName, field) -> {
                String beanN = StringUtils.toLowerCaseFirstOne(field.getDeclaringClass().getSimpleName());
                Object bean = this.getBean(beanN);
                Object o = this.getBean(beanName);
                if (beanId.equals(beanName)) {
                    field.setAccessible(true);
                    try {
                        //把事务的代理对象重新设置到调用它的类中
                        field.set(bean, o);
                        this.iOCContainer.packObject(beanN, bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }


    /**
     * 实例化对象
     *
     * @param beanId
     * @param aClass
     */
    private void createInstance(String beanId, Class<?> aClass) {
        if (beanId.isEmpty()) {
            beanId = aClass.getSimpleName();
            beanId = StringUtils.toLowerCaseFirstOne(beanId);
            if (this.iOCContainer.isContains(beanId)) {
                throw new RuntimeException("当前Bean已经存在");
            }
        }
        //真正去取实例化对象并且保存到ioc容器中的操作
        this.doCreateInstance(beanId, aClass);
    }
}
