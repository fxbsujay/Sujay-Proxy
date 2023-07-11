package com.susu.proxy.core.netty.msg;

import com.google.protobuf.MessageLite;
import com.susu.proxy.core.common.eum.PacketType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import com.susu.proxy.core.common.Constants;
import java.util.List;

/**
 * <p>Description: 网络请求</p>
 * <p>Description:  network Request</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetRequest {

    /**
     * 管道
     */
    private ChannelHandlerContext ctx;

    /**
     * 请求序列号
     */
    private long requestSequence;

    /**
     * 请求包
     */
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
        sendResponse(new byte[0]);
    }

    /**
     * 发送响应
     *
     * @param response 响应
     */
    public void sendResponse(MessageLite response) {
        byte[] body = response == null ? new byte[0] : response.toByteArray();
        sendResponse(body);
    }

    /**
     * 发送响应
     *
     * @param response 响应
     */
    public void sendResponse(String response) {
        byte[] body = response == null ? new byte[0] : response.getBytes(Constants.DEFAULT_ENCODING);
        sendResponse(body);
    }

    public void sendResponse(byte[] body) {
        NetPacket packet = NetPacket.buildPacket(body, PacketType.getEnum(request.getType()));
        List<NetPacket> responses = packet.partitionChunk(request.isSupportChunked(), Constants.CHUNKED_SIZE);
        if (responses.size() > 1) {
            log.info("Split response data packet, number {}", responses.size());
        }
        for (NetPacket res : responses) {
            sendResponse(res, requestSequence);
        }
    }

    public void sendResponse(NetPacket response, Long sequence) {
        if (sequence != null) {
            response.setSequence(sequence);
        }
        ctx.writeAndFlush(response);
    }
}
