package com.susu.proxy.server;


import com.susu.proxy.server.web.TomcatServer;
import com.susu.proxy.server.web.servlet.ResourcesDispatcherServlet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class TomcatServerTest {

    @Test
    public void Tomcat() throws IOException {
        TomcatServer server = new TomcatServer(9999);
        ResourcesDispatcherServlet servlet = new ResourcesDispatcherServlet();
        server.addServlet("/", servlet);
        server.start();
        System.in.read();
    }
}
