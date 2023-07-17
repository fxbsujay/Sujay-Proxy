package com.susu.proxy.server.web.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: Spring Bean Factory</p>
 * <p>Description: 缩小版的 Spring 容器 </p>
 *
 * @author sujay
 * @since 13:13 2023/07/17
 * @version 1.0 JDK1.8
 */
public class DispatchComponentProvider {

    public static volatile DispatchComponentProvider INSTANCE = null;

    /**
     * 组件集合
     */
    private final Map<String, Object> components = new HashMap<>();

    public static DispatchComponentProvider getInstance() {
        if (INSTANCE == null) {
            synchronized (DispatchComponentProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DispatchComponentProvider();
                }
            }
        }
        return INSTANCE;
    }

    public void addComponent(Object... objs) {
        for (Object obj : objs) {
            components.put(obj.getClass().getSimpleName(), obj);
        }
    }

    public Object getComponent(String key) {
        return components.get(key);
    }
}
