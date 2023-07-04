package com.susu.proxy.client;

import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.TaskScheduler;

public class ProxyClient {

    private NetClient netClient;

    private final TaskScheduler taskScheduler;

    public ProxyClient(TaskScheduler taskScheduler) {
        this.netClient = new NetClient(AppConfig.name, taskScheduler);
        this.taskScheduler = taskScheduler;
    }


    /**
     * 停止服务
     */
    public void shutdown() {
        if (netClient != null) {
            netClient.shutdown();
        }
    }

    private void onTrackerResponse(NetRequest request) throws Exception {


    }
}
