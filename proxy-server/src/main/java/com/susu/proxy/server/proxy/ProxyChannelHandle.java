package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.common.eum.PacketType;
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

    private final ThreadPoolExecutor executor;

    private final TaskScheduler taskScheduler;

    public ProxyChannelHandle(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;

        this.executor = new ThreadPoolExecutor(Constants.HANDLE_THREAD_EXECUTOR_CORE_SIZE,Constants.HANDLE_THREAD_EXECUTOR_CORE_SIZE_MAX,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Constants.HANDLE_THREAD_EXECUTOR_QUEUE_SIZE_MAX));
    }

    @Override
    protected boolean handlePackage(ChannelHandlerContext ctx, NetPacket packet) throws Exception {

        PacketType packetType = PacketType.getEnum(packet.getType());
        NetRequest request = new NetRequest(ctx, packet);
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


}
