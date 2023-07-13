package com.susu.proxy.server.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.model.HeartbeatRequest;
import com.susu.proxy.core.common.model.HeartbeatResponse;
import com.susu.proxy.core.common.model.RegisterRequest;
import com.susu.proxy.core.common.model.RegisterResponse;
import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProxyChannelHandle extends AbstractChannelHandler {

    /**
     * 消息处理执行器
     */
    private final ThreadPoolExecutor executor;

    /**
     * 共享线程池
     */
    private final TaskScheduler taskScheduler;


    private final ProxyClientManager clientManager;

    public ProxyChannelHandle(ProxyClientManager clientManager, TaskScheduler taskScheduler) {
        this.clientManager = clientManager;
        this.taskScheduler = taskScheduler;
        this.executor = new ThreadPoolExecutor(Constants.HANDLE_THREAD_EXECUTOR_CORE_SIZE,Constants.HANDLE_THREAD_EXECUTOR_CORE_SIZE_MAX,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Constants.HANDLE_THREAD_EXECUTOR_QUEUE_SIZE_MAX));
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
                storageHeartbeatHandle(request);
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
        return executor;
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
        RegisterResponse response = RegisterResponse.newBuilder().setIsSuccess(register).build();
        request.sendResponse(response);
    }

    /**
     * <p>Description: 客户端心跳请求处理</p>
     * <p>Description: Client heartbeat request processing </p>
     *
     * @param request NetWork Request 网络请求
     * @throws InvalidProtocolBufferException protobuf error
     */
    private void storageHeartbeatHandle(NetRequest request) throws InvalidProtocolBufferException {
        HeartbeatRequest heartbeatRequest = HeartbeatRequest.parseFrom(request.getRequest().getBody());
        Boolean isSuccess = clientManager.heartbeat(heartbeatRequest.getHostname());
        HeartbeatResponse response = HeartbeatResponse.newBuilder()
                .setIsSuccess(isSuccess)
                .build();
        request.sendResponse(response);
    }
}
