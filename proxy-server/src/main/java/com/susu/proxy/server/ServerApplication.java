package com.susu.proxy.server;

import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.client.MasterChannelHandle;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.server.client.MasterServer;
import com.susu.proxy.server.entity.PortMapping;
import com.susu.proxy.server.proxy.PortInstantiationStrategy;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ServerApplication {

    private final TaskScheduler taskScheduler;

    private MasterClientManager clientManager;

    private final MasterChannelHandle handle;

    private final  PortInstantiationStrategy strategy;

    private final MasterServer server;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public static void main(String[] args ) {

        ConfigLoadUtils.refreshConfig();
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

    public ServerApplication() {
        this.taskScheduler = new TaskScheduler("Server-Scheduler");
        this.clientManager = new MasterClientManager();
        this.handle = new MasterChannelHandle(clientManager, taskScheduler);
        this.server = new MasterServer(taskScheduler, handle);
        this.strategy = new PortInstantiationStrategy(clientManager, taskScheduler);
    }

    /**
     * 启动
     */
    public void start() throws InterruptedException {
        if (started.compareAndSet(false, true)) {
            this.server.start();
        }
    }

    /**
     * 停机
     */
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            this.taskScheduler.shutdown();
            this.server.shutdown();
        }
    }

}
