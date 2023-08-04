package com.susu.proxy.server;

import com.susu.proxy.server.web.TomcatServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TomcatServerTest {

    @Test
    public void Tomcat() {
        TomcatServer server = new TomcatServer(9999);
        server.start();
    }
}
