package com.susu.proxy.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.susu.proxy.client.proxy.ProxyManager;
import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.model.HeartbeatResponse;
import com.susu.proxy.core.common.model.ProxyRequest;
import com.susu.proxy.core.common.model.RegisterRequest;
import com.susu.proxy.core.common.model.RegisterResponse;
import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.config.ClientConfig;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.HeartbeatTask;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;


/**
 * <p>Description: 通信客户端，负责调度任务，同步信息 </p>
 *
 * @author sujay
 * @since 18:13 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class MasterClient {

    /**
     * 客户端心跳时间
     */
    private final int heartbeatInterval;

    private NetClient netClient;

    private final TaskScheduler taskScheduler;

    private ProxyManager proxyManager;

    /**
     * 用来客户端发送心跳
     */
    private ScheduledFuture<?> scheduledFuture;

    public MasterClient(TaskScheduler taskScheduler) {
        this.netClient = new NetClient(AppConfig.name, taskScheduler);
        this.taskScheduler = taskScheduler;
        this.heartbeatInterval = ClientConfig.heartbeatInterval;
    }

    public void setProxyManager(ProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }

    /**
     * 启动服务
     */
    public void start () {
        this.netClient.addPackageListener(this::onTrackerResponse);
        this.netClient.addConnectListener( isConnected -> {
            log.info("Client Connect Start : {}", isConnected);
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
        log.info("Client Register {}:{}", ClientConfig.serverIp, ClientConfig.serverPort);
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

    /**
     * <p>Description: 处理 Proxy Server 返回的信息</p>
     * <p>Description: Processing requests returned by Proxy Server</p>
     *
     * @param request NetWork Request 网络请求
     */
    private void onTrackerResponse(NetRequest request) throws Exception {
        PacketType packetType = PacketType.getEnum(request.getRequest().getType());
        switch (packetType) {
            case SERVICE_REGISTER:
                serviceRegisterResponse(request);
                break;
            case SERVICE_HEART_BEAT:
                serviceHeartbeatResponse(request);
                break;
            case SERVER_CREATE_PROXY:
                serverCreateProxy(request);
                break;
            default:
                break;
        }

    }

    /**
     * <p>Description: 处理 注册 返回的消息</p>
     * <p>Description: Processing messages returned from registration</p>
     *
     * @param request NetWork Request 网络请求
     */
    private void serviceRegisterResponse(NetRequest request) throws InvalidProtocolBufferException {
        ChannelHandlerContext ctx = request.getCtx();
        RegisterResponse registerResponse = RegisterResponse.parseFrom(request.getRequest().getBody());
        if (scheduledFuture == null && registerResponse.getIsSuccess()) {
            log.info("Start the scheduled task to send heartbeat, heartbeat interval: [interval={}ms]", ClientConfig.heartbeatInterval);
            scheduledFuture = ctx.executor().scheduleAtFixedRate(new HeartbeatTask(ctx),
                    0,ClientConfig.heartbeatInterval, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * <p>Description: 处理 心跳请求 返回的消息</p>
     * <p>Description: Processing messages returned from heartbeat</p>
     *
     * @param request NetWork Request 网络请求
     */
    private void serviceHeartbeatResponse(NetRequest request) throws Exception {
        HeartbeatResponse response = HeartbeatResponse.parseFrom(request.getRequest().getBody());
        if (!response.getIsSuccess()) {
            log.warn("Client heartbeat fail!! ReRegister");
            register();
        }
    }

    private void serverCreateProxy(NetRequest request) throws Exception {
        ProxyRequest response = ProxyRequest.parseFrom(request.getRequest().getBody());
        String ip = response.getClientIp();
        int port = response.getClientPort();
        proxyManager.connect(ip, port);
    }
}
