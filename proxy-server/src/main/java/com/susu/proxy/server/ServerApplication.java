package com.susu.proxy.server;

import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.proxy.ProxyChannelHandle;
import com.susu.proxy.server.proxy.ProxyServer;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ServerApplication {

    private final TaskScheduler taskScheduler;


    private final ProxyChannelHandle handle;

    private final ProxyServer server;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public static void main( String[] args ) {

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
        this.handle = new ProxyChannelHandle(taskScheduler);
        this.server = new ProxyServer(taskScheduler, handle);
    }

    /**
     * 启动
     */
    public void start() {
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
