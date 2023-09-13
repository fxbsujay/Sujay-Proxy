package com.susu.proxy.core.common.utils;

import com.alibaba.fastjson.JSON;
import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    public static <T> T load(String path) {

        File file = new File(path);
        if (!file.exists()) {
            log.error("The {} file under the resource directory cannot be found !!", path);
            System.exit(1);
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
        } catch (FileNotFoundException e) {
            log.error("exception for read file");
            System.exit(1);
        }

        return new Yaml().load(fileReader);
    }

    public static <T> T convert(Object obj, Class<T> t) {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), t);
    }

    public static Set<Class<?>> loadConfigurationClass() {
        return ClassUtils.scanPackageByAnnotation("com.susu.proxy", Configuration.class);
    }

    public static void refreshConfig(String[] args) {

        System.out.println("       _            __                              \n" +
                "  ___ (_)_ _  ___  / /__ _______  _______ __ ____ __\n" +
                " (_-</ /  ' \\/ _ \\/ / -_)___/ _ \\/ __/ _ \\\\ \\ / // /\n" +
                "/___/_/_/_/_/ .__/_/\\__/   / .__/_/  \\___/_\\_\\\\_, / \n" +
                "           /_/            /_/                /___/  ");

        String configPath = null;
        for (String arg : args) {
            String[] split = arg.split("=");
            if ("config".equals(split[0])) {
                configPath = split[1];
                log.info("Load configuration file: {}", configPath);
                break;
            }
        }

        Map<String, Object> parameters = StringUtils.isNotBlank(configPath) ? load(configPath) : load();
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
