package com.susu.proxy.server.web.servlet;

import com.susu.proxy.core.common.utils.ClassUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.server.web.annotation.Autowired;
import com.susu.proxy.server.web.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServlet;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractDispatcherServlet extends HttpServlet {

    /**
     * BASE_PACKAGE 路径下扫描到的所有类名
     */
    private Set<Class<?>> classes = new HashSet<>();

    /**
     * Tomcat Servlet 扫描路径
     */
    private static final String BASE_PACKAGE = "com.susu.proxy.server.web.servlet";

    /**
     *  key:    类名
     *  value:  反射生成的bean对象名称，和spring的bean是一个概念
     */
    private Map<String, String> classNameToBeanNameMap = new ConcurrentHashMap<>();

    /**
     *  key:    bean名称
     *  value:  发射生成的类对象
     */
    private Map<String, Object> beanNameToInstanceMap = new ConcurrentHashMap<>();

    public AbstractDispatcherServlet() {
        this(BASE_PACKAGE);
    }

    public AbstractDispatcherServlet(String basePackage) {
        try {
            initController(basePackage);
            injectControllerComponent();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void initController(String basePackage) throws InstantiationException, IllegalAccessException {
        this.classes = ClassUtils.scanPackageByAnnotation(basePackage, RestController.class);
        for (Class<?> clazz : this.classes) {
            RestController annotation = clazz.getAnnotation(RestController.class);
            String beanName = annotation.value();
            if (StringUtils.isBlank(beanName)) {
                beanName = clazz.getSimpleName();
            }
            beanNameToInstanceMap.put(beanName, clazz.newInstance());
            classNameToBeanNameMap.put(clazz.getCanonicalName(), beanName);
        }
    }

    private void injectControllerComponent() throws IllegalAccessException {
        for (Map.Entry<String, Object> entry : beanNameToInstanceMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }

                Autowired annotation = field.getAnnotation(Autowired.class);
                String needInjectBeanName = annotation.value();

                if (needInjectBeanName.length() == 0) {
                    needInjectBeanName = field.getType().getSimpleName();
                }
                field.setAccessible(true);
                Object component = DispatchComponentProvider.getInstance().getComponent(needInjectBeanName);
                if (component == null) {
                    throw new IllegalArgumentException("Can not find component " + needInjectBeanName);
                }
                field.set(entry.getValue(), component);

                if (log.isDebugEnabled()) {
                    log.debug("Inject controller components：[{}] ", entry.getValue().getClass().getSimpleName() + "#" + field.getName());
                }
            }
        }
    }

    private void createRequestMapping() {

    }
}
