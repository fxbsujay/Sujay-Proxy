package com.susu.proxy.core.common.utils;

import com.alibaba.fastjson.JSON;
import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ConfigLoadUtils {

    /**
     * 配置文件名称
     */
    private static final String CONFIG_FILE_NAME = Constants.CONFIG_FILE_NAME;

    public static <T> T load() {
        return load(CONFIG_FILE_NAME);
    }

    public static <T> T load(String path) {
        InputStream in = null;
        try {
            in = ConfigLoadUtils.class.getResourceAsStream(CONFIG_FILE_NAME);
            if (in == null) {
                log.error("The {} file under the resource directory cannot be found !!", CONFIG_FILE_NAME);
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("exception for read file");
            System.exit(1);
        }
        return new Yaml().load(in);
    }

    public static <T> T convert(Object obj, Class<T> t) {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), t);
    }

    public static Set<Class<?>> loadConfigurationClass() {
        return  ClassUtils.scanPackageByAnnotation("", Configuration.class);
    }

    public static void refreshConfig() {
        Map<String, Object> parameters = load();
        Set<Class<?>> classes = loadConfigurationClass();
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
