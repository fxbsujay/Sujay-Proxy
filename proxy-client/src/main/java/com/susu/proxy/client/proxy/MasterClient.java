package com.susu.proxy.client.proxy;

import com.google.protobuf.InvalidProtocolBufferException;
import com.susu.proxy.client.proxy.ProxyManager;
import com.susu.proxy.core.common.entity.PortMapping;
import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.eum.ProxyStateType;
import com.susu.proxy.core.common.model.*;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.config.ClientConfig;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.HeartbeatTask;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
                serverCreateProxyResponse(request);
                break;
            case SERVER_SYNC_PROXY:
                ProxiesRequest proxies = ProxiesRequest.parseFrom(request.getRequest().getBody());
                proxyManager.syncProxies(convertProxiesRequest(proxies.getProxiesList()));
                break;
            case SERVER_REMOVE_PROXY:
                CloseProxyRequest closeProxyRequest = CloseProxyRequest.parseFrom(request.getRequest().getBody());
                proxyManager.remove(closeProxyRequest.getClientPort());
                break;
            case TRANSFER_NETWORK_PACKET:
                transferServerNetworkPacket(request);
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

        proxyManager.syncProxies(convertProxiesRequest(response.getProxiesList()));
    }

    /**
     * <p>Description: 接收创建代理的请求，连接真实服务</p>
     * <p>Description: Receive a request to create a proxy to connect to the real service</p>
     *
     * @param request NetWork Request 网络请求
     */
    private void serverCreateProxyResponse(NetRequest request) throws Exception {
        ProxyRequest response = ProxyRequest.parseFrom(request.getRequest().getBody());

        PortMapping mapping = new PortMapping();
        mapping.setClientIp(response.getClientIp());
        mapping.setProtocol(ProtocolType.getEnum(response.getProtocol()));
        mapping.setClientPort(response.getClientPort());
        mapping.setServerPort(response.getServerPort());

        proxyManager.create(mapping);
    }

    /**
     * <p>Description: 转发服务端代理消息</p>
     *
     * @param request NetWork Request 网络请求
     */
    public void transferServerNetworkPacket(NetRequest request) {
        NetPacket packet = request.getRequest();
        String address = packet.getAddress();
        if (StringUtils.isEmpty(address)) {
            return;
        }

        byte[] body = packet.getBody();
        ByteBuf buf = request.getCtx().alloc().buffer(body.length);
        buf.writeBytes(body);
        proxyManager.send(address, buf);
    }

    private List<PortMapping> convertProxiesRequest(List<ProxyRequest> proxies) {
        List<PortMapping> mappings = new ArrayList<>();

        if (proxies == null || proxies.isEmpty()) {
            return mappings;
        }

        for (ProxyRequest proxy : proxies) {
            PortMapping mapping = new PortMapping();
            mapping.setClientIp(proxy.getClientIp());
            mapping.setProtocol(ProtocolType.getEnum(proxy.getProtocol()));
            mapping.setClientPort(proxy.getClientPort());
            mapping.setServerPort(proxy.getServerPort());
            mappings.add(mapping);
        }

        return mappings;
    }

    public void forwardMessageRequest(String address, byte[] bytes) throws InterruptedException {

        NetPacket packet = NetPacket.buildPacket(bytes, PacketType.TRANSFER_NETWORK_PACKET);
        packet.setAddress(address);
        netClient.send(packet);
    }

    /**
     * 访客连接失败通知
     *
     * @param visitorId 访客ID
     */
    public void connectionClosureNotificationRequest(String visitorId) {
        ConnectionClosureNotificationRequest request = ConnectionClosureNotificationRequest.newBuilder()
                .setVisitorId(visitorId)
                .build();
        NetPacket packet = NetPacket.buildPacket(request.toByteArray(), PacketType.CONNECTION_CLOSURE_NOTIFICATION);
        try {
            netClient.send(packet);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
