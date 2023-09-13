package com.susu.proxy.server;

import com.susu.proxy.server.web.TomcatServer;
import org.junit.Test;

public class TomcatServerTest {

    @Test
    public void Tomcat() {
        TomcatServer server = new TomcatServer(9999);
        server.start();
    }
}
