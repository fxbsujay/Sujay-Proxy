package com.susu.proxy.core.netty.msg;

import com.susu.proxy.core.common.eum.PacketType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 网络请求</p>
 * <p>Description:  network Request</p>
 *
 * @author sujay
 * @version 0:31 2022/7/9
 */
@Slf4j
public class NetRequest {


    private ChannelHandlerContext ctx;

    private final long requestSequence;

    private NetPacket request;


    public NetRequest(ChannelHandlerContext ctx, NetPacket request) {
        this.ctx = ctx;
        this.requestSequence = request.getSequence();
        this.request = request;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public NetPacket getRequest() {
        return request;
    }

    /**
     * 发送响应
     */
    public void sendResponse() {
        sendResponse(null);
    }

    /**
     * 发送响应
     *
     * @param response 响应
     */
    public void sendResponse(String response) {
        NetPacket packet = NetPacket.buildPacket(response, PacketType.getEnum(request.getType()));
        sendResponse(packet, requestSequence);
    }

    public void sendResponse(NetPacket response, Long sequence) {
        if (sequence != null) {
            response.setSequence(sequence);
        }
        ctx.writeAndFlush(response);
    }
}
