package com.susu.proxy.server.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.susu.proxy.core.common.entity.PortMapping;
import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.eum.ProxyStateType;
import com.susu.proxy.core.common.model.*;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.proxy.PortInstantiationStrategy;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Description: 代理客户端的消息处理器</p>
 *
 * @author sujay
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Slf4j
public class MasterChannelHandle extends AbstractChannelHandler {

    /**
     * 共享线程池
     */
    private final TaskScheduler taskScheduler;

    /**
     * 代理策略
     */
    private final PortInstantiationStrategy strategy;

    /**
     * 客户端管理器
     */
    private final MasterClientManager clientManager;

    public MasterChannelHandle(PortInstantiationStrategy strategy, MasterClientManager clientManager, TaskScheduler taskScheduler) {
        this.strategy = strategy;
        this.clientManager = clientManager;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String hostname = clientManager.disconnected(ctx);
        if (StringUtils.isNotBlank(hostname)) {
            strategy.setConnectState(hostname, ProxyStateType.CLOSE);
        }
    }

    @Override
    protected boolean handlePackage(ChannelHandlerContext ctx, NetPacket packet) throws Exception {

        PacketType packetType = PacketType.getEnum(packet.getType());
        NetRequest request = new NetRequest(ctx, packet);

        switch (packetType) {
            case SERVICE_REGISTER:
                serviceRegisterHandle(request);
                break;
            case SERVICE_HEART_BEAT:
                serviceHeartbeatHandle(request);
                break;
            case CONNECTION_CLOSURE_NOTIFICATION:
                strategy.close(request.getRequest().getVisitorId());
                break;
            case TRANSFER_NETWORK_PACKET:
                transferServerPacketRequest(request);
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    protected Set<Integer> interestPackageTypes() {
        return new HashSet<>();
    }

    @Override
    public ThreadPoolExecutor getExecutor() {
        return taskScheduler.getExecutor();
    }

    /**
     * <p>Description: 客户端注册请求处理</p>
     * <p>Description: Client registration request processing </p>
     *
     * @param request NetWork Request 网络请求
     */
    private void serviceRegisterHandle(NetRequest request) throws InvalidProtocolBufferException {
        RegisterRequest registerRequest = RegisterRequest.parseFrom(request.getRequest().getBody());
        boolean register = clientManager.register(registerRequest, request.getCtx());
        RegisterResponse response = RegisterResponse.newBuilder()
                .setIsSuccess(register)
                .build();
        request.sendResponse(response);
    }

    /**
     * <p>Description: 客户端心跳请求处理</p>
     * <p>Description: Client heartbeat request processing </p>
     *
     * @param request NetWork Request 网络请求
     * @throws InvalidProtocolBufferException protobuf error
     */
    private void serviceHeartbeatHandle(NetRequest request) throws InvalidProtocolBufferException {
        HeartbeatRequest heartbeatRequest = HeartbeatRequest.parseFrom(request.getRequest().getBody());
        String hostname = heartbeatRequest.getHostname();
        Boolean isSuccess = clientManager.heartbeat(hostname);

        List<ProxyRequest> proxies = new LinkedList<>();
        for (PortMapping mapping : strategy.getAllMapping()) {
            if (mapping.getClientIp().equals(hostname)) {

                ProxyRequest proxy = ProxyRequest.newBuilder()
                        .setProtocol(mapping.getProtocol().getName())
                        .setClientPort(mapping.getClientPort())
                        .setServerPort(mapping.getServerPort())
                        .setClientIp(mapping.getClientIp())
                        .build();

                proxies.add(proxy);
            }
        }

        HeartbeatResponse response = HeartbeatResponse.newBuilder()
                .setIsSuccess(isSuccess)
                .addAllProxies(proxies)
                .build();
        request.sendResponse(response);
    }

    /**
     * <p>Description: 转发服务端代理消息</p>
     *
     * @param request NetWork Request 网络请求
     */
    public void transferServerPacketRequest(NetRequest request) {
        NetPacket packet = request.getRequest();
        String visitorId = packet.getVisitorId();
        if (StringUtils.isEmpty(visitorId)) {
            return;
        }

        byte[] body = packet.getBody();
        ByteBuf buf = request.getCtx().alloc().buffer(body.length);
        buf.writeBytes(body);

        log.info("Received server side message: {}", visitorId);
        strategy.send(visitorId, buf);
    }
}
