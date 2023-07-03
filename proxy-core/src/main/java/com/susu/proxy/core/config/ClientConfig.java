package com.susu.proxy.core.config;

import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;

@Data
@Configuration("client")
public class ClientConfig {

    public static String serverIp = "localhost";

    public static Integer serverPort = 8899;
}
