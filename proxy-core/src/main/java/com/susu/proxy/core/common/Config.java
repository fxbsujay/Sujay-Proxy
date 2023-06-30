package com.susu.proxy.core.common;

import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@Data
@Configuration
public class Config {

    /**
     * 服务名称
     */
    public static String name;

    /**
     * 客户端配置
     */
    public static Client client;

    static {
        Map<String, Object> parameters = ConfigLoadUtils.load();
        Object clientObj = parameters.get("client");

        name = (String) parameters.getOrDefault("name","proxy");
        if (clientObj != null) {
            client = ConfigLoadUtils.convert(clientObj, Client.class);
        }

    }

    @Data
    public static class Client {
        public String serverIp = "localhost";
        public Integer serverPort = 8899;
    }

    public static class Server {
        public Integer port = 8899;
    }

}
