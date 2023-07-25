package com.susu.proxy.server.web;

import com.susu.proxy.core.common.Constants;
import com.susu.proxy.server.web.servlet.CorsFilter;
import com.susu.proxy.server.web.entity.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * <p>Description: Tomcat  Http服务端</p>
 *
 * @author sujay
 * @version 14:48 2022/8/12
 */
@Slf4j
public class TomcatServer {

    /**
     * HTTP 端口
     */
    private final int port;

    private final String basedir;

    private final Tomcat tomcat;

    private final List<Servlet> servlets = new ArrayList<>();

    public TomcatServer(int port) {;
        this.tomcat = new Tomcat();
        this.basedir = createTempDir();
        this.port = port;
    }

    public void start() {

        tomcat.setBaseDir(basedir);
        tomcat.setHostname("localhost");
        tomcat.setPort(port);

        Context context = initializeServlet();
        context.addWatchedResource("");

        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(new CorsFilter());
        filterDef.setFilterName("CorsFilter");
        FilterMap filterMap = new FilterMap();
        filterMap.addURLPatternDecoded("/*");
        filterMap.addServletName("*");
        filterMap.setFilterName("CorsFilter");
        filterMap.setCharset(Constants.DEFAULT_ENCODING);
        context.addFilterDef(filterDef);
        context.addFilterMap(filterMap);

        try {
            tomcat.init();
            tomcat.start();
            log.info("Tomcat Server started on port：{}", port);
            tomcat.getServer().await();
        } catch (Exception e) {
            log.error("Tomcat start fail：", e);
            System.exit(0);
        }
    }

    /**
     * 初始化 Servlet
     */
    private Context initializeServlet() {
        Context context = tomcat.addContext("", null);
        if (!servlets.isEmpty()) {
            for (Servlet servlet : servlets) {
                Tomcat.addServlet(context, servlet.getName(),servlet.getServlet());
                context.addServletMappingDecoded(servlet.getPath(), servlet.getName());
            }
        }
        return context;
    }

    /**
     * 关机
     */
    public void shutdown() {
        try {
            tomcat.stop();
        } catch (Exception e) {
            log.error("Shutdown Tomcat Server：", e);
        }
    }

    /**
     * 创建临时目录
     */
    private String createTempDir() {
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory(  "Tomcat," + this.port + ".").toFile();
            tempDir.deleteOnExit() ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempDir.getAbsolutePath();
    }

    public void addServlet(String path, HttpServlet servlet) {
        addServlets(Collections.singletonList(new Servlet(path, servlet)));
    }

    public void addServlet(Servlet servlet) {
        addServlets(Collections.singletonList(servlet));
    }

    public void addServlets(List<Servlet> servlets) {
        if (servlets == null || servlets.isEmpty()) {
            return;
        }

        for (Servlet servlet : servlets) {
            if (this.servlets.contains(servlet)) {
                continue;
            }
            this.servlets.add(servlet);
        }
    }
}
