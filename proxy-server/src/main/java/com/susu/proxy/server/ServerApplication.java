package com.susu.proxy.server;

import cn.hutool.core.util.ClassUtil;
import com.susu.proxy.core.common.Config;
import com.susu.proxy.core.stereotype.Configuration;


public class ServerApplication {
    public static void main( String[] args ) {

        System.out.println(Config.client.serverIp);
        ClassUtil.scanPackageByAnnotation("com", Configuration.class);
    }
}
