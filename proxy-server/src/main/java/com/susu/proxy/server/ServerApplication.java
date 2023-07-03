package com.susu.proxy.server;

import com.susu.proxy.core.common.Config;
import com.susu.proxy.core.config.ClientConfig;

public class ServerApplication {
    public static void main( String[] args ) {

        System.out.println(Config.name);

        System.out.println(ClientConfig.serverIp);
    }
}
