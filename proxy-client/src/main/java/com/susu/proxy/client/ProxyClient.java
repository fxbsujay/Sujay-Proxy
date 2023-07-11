package com.susu.proxy.client;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.model.RegisterRequest;
import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.config.ClientConfig;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyClient {

    private NetClient netClient;

    private final TaskScheduler taskScheduler;

    /**
     * 用来客户端发送心跳
     */
    private ScheduledFuture<?> scheduledFuture;

    public ProxyClient(TaskScheduler taskScheduler) {
        this.netClient = new NetClient(AppConfig.name, taskScheduler);
        this.taskScheduler = taskScheduler;
    }


    /**
     * 启动服务
     */
    public void start() {
        this.netClient.addPackageListener(this::onTrackerResponse);
        this.netClient.addConnectListener( isConnected -> {
            log.info("[{}] Client Connect Start : {}", AppConfig.name, isConnected);
            if (isConnected) {
                register();
            } else {
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                    scheduledFuture = null;
                }
            }
        });

        this.netClient.addClientFailListener(() -> {
            log.info("Server Down {}:{} !!", ClientConfig.serverIp, ClientConfig.serverPort);
        });
        this.netClient.start(ClientConfig.serverIp, ClientConfig.serverPort);
    }

    private void register() throws InterruptedException {
        RegisterRequest request = RegisterRequest.newBuilder()
                .setName(AppConfig.name)
                .setHostname(AppConfig.localhost)
                .build();
        NetPacket packet = NetPacket.buildPacket(request.toByteArray(), PacketType.SERVICE_REGISTER);
        log.info("[{}] Client Register {}:{}", AppConfig.name, ClientConfig.serverIp, ClientConfig.serverPort);
        netClient.send(packet);
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
