package com.susu.proxy.core.task;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.model.HeartbeatRequest;
import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.netty.msg.NetPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送心跳的请求
 */
@Slf4j
public class HeartbeatTask implements Runnable {

    private ChannelHandlerContext ctx;

    public HeartbeatTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        HeartbeatRequest request = HeartbeatRequest.newBuilder()
                .setHostname(AppConfig.localhost)
                .build();
        NetPacket nettyPacket = NetPacket.buildPacket(request.toByteArray(), PacketType.SERVICE_HEART_BEAT);
        ctx.writeAndFlush(nettyPacket);
    }
}