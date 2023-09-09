package com.susu.proxy.core.common;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.MessageLite;
import com.susu.proxy.core.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
public class LocalStorage {

    public static void loadWrite(String path, Object obj) {
        String data = JSONObject.toJSONString(obj);

        try {
            FileUtils.writeFile(path, true, ByteBuffer.wrap(data.getBytes()));
        } catch (IOException e) {
            log.warn("Failed to store data locally, location: {}", path);
        }
    }

    public static <T> List<T> loadReady(String path, Class<T> tClass) {
        File file = new File(path);

        if (!file.exists()) {
            return null;
        }

        String data = null;
        try {
           data = FileUtils.readString(path);
        } catch (IOException e) {
            log.warn("Failed to load data locally, location: {}", path);
        }

        return JSONObject.parseArray(data, tClass);
    }
}
