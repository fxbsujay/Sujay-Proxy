package com.susu.proxy.core.common;

import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Slf4j
@Data
public class Config {

    /**
     * 服务名称
     */
    public static String name;

    static {
        Map<String, Object> parameters = ConfigLoadUtils.load();
        Set<Class<?>> classes = ConfigLoadUtils.loadConfigurationClass();
        for (Class<?> aClass : classes) {
            Configuration annotation = aClass.getAnnotation(Configuration.class);
            Object values = parameters.get(annotation.value());
            if (values != null) {
                if (!((Map<?, ?>) values).isEmpty()) {
                    Field[] fields = aClass.getDeclaredFields();
                    for (Field field : fields) {
                        Object o = ((Map<?, ?>) values).get(field.getName());
                        if (o != null) {
                            try {
                                Object instance = aClass.newInstance();
                                field.set(instance,o);
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }
}
