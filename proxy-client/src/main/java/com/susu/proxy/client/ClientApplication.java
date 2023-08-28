package com.susu.proxy.client;

import com.susu.proxy.client.proxy.ProxyManager;
import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ClientApplication {

    private final MasterClient client;

    private final TaskScheduler taskScheduler;

    private final ProxyManager proxyManager;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public ClientApplication() {
        this.taskScheduler = new TaskScheduler("Client-Scheduler");
        this.client = new MasterClient(taskScheduler);
        this.proxyManager = new ProxyManager(client);
    }

    public static void main( String[] args ) {
        ConfigLoadUtils.refreshConfig();
        ClientApplication application = new ClientApplication();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));
            application.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Client Application Start Error!!");
            System.exit(1);
        }
    }

    /**
     * 启动
     */
    public void start() throws InterruptedException {
        if (started.compareAndSet(false, true)) {
            this.client.start();
        }
        while (started.get()) {
            Thread.sleep(100);
        }
    }

    /**
     * 停机
     */
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            this.taskScheduler.shutdown();
            this.client.shutdown();
        }
    }
}
