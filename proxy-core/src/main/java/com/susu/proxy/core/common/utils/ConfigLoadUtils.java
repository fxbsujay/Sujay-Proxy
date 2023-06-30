package com.susu.proxy.core.common.utils;

import com.alibaba.fastjson.JSON;
import com.susu.proxy.core.common.Config;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;

@Slf4j
public class ConfigLoadUtils {

    /**
     * 配置文件名称
     */
    private static final String CONFIG_FILE_NAME = "/application.yaml";

    public static <T> T load() {
        return load(CONFIG_FILE_NAME);
    }

    public static <T> T load(String path) {
        InputStream in = null;
        try {
            in = Config.class.getResourceAsStream(CONFIG_FILE_NAME);
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
}
