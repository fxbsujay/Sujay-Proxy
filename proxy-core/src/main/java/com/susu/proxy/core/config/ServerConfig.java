package com.susu.proxy.core.config;

import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;

@Data
@Configuration("server")
public class ServerConfig {

    public static Integer port = 8899;

    public static Integer httpPort = 8890;

    public static String username = "admin";

    public static String password = "admin";
}
