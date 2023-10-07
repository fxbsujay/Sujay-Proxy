package com.susu.proxy.server;

import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.config.ServerConfig;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.client.MasterChannelHandle;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.server.client.MasterServer;
import com.susu.proxy.server.proxy.PortInstantiationStrategy;
import com.susu.proxy.server.web.TomcatServer;
import com.susu.proxy.server.web.service.AuthService;
import com.susu.proxy.server.web.service.ClientService;
import com.susu.proxy.server.web.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        ConfigLoadUtils.refreshConfig(args);
        ServerApplication application = new ServerApplication();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));
            application.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Server Application Start Error!!");
            System.exit(1);
        }
    }

    /**
     * 线程池
     */
    private final TaskScheduler taskScheduler;

    /**
     * 代理服务器
     */
    private final MasterServer server;

    /**
     * Api 服务器
     */
    private final TomcatServer tomcatServer;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public ServerApplication() {
        this.taskScheduler = new TaskScheduler("Server-Scheduler");
        MasterClientManager clientManager = new MasterClientManager(taskScheduler);
        PortInstantiationStrategy strategy = new PortInstantiationStrategy(clientManager, taskScheduler);
        this.server = new MasterServer(taskScheduler, new MasterChannelHandle(strategy, clientManager));
        this.tomcatServer = new TomcatServer(ServerConfig.httpPort);
        new ClientService(clientManager);
        new ProxyService(strategy, clientManager);
        new AuthService();
    }

    /**
     * 启动
     */
    public void start() throws InterruptedException {
        if (started.compareAndSet(false, true)) {
            this.server.start();
            this.tomcatServer.start();
        }
    }

    /**
     * 停机
     */
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            this.taskScheduler.shutdown();
            this.server.shutdown();
            this.tomcatServer.shutdown();
        }
    }

}
