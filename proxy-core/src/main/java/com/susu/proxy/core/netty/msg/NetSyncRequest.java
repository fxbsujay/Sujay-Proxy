package com.susu.proxy.core.netty.msg;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: 网络同步请求</p>
 * <p>Description: network Sync Request</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetSyncRequest {


    /**
     * 所有等待处理的同步请求
     */
    private Map<Long, NetSyncRequestPromise> promises = new ConcurrentHashMap<>();

    private SocketChannel socketChannel;

    public NetSyncRequest(TaskScheduler taskScheduler) {
        taskScheduler.schedule("NetSyncRequest", this::checkRequestTimeout, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * 定时检测请求是否超时，避免请求被hang死
     */
    private void checkRequestTimeout() {
        synchronized (this) {
            for (Map.Entry<Long, NetSyncRequestPromise> entry : promises.entrySet()) {
                NetSyncRequestPromise value = entry.getValue();
                if (value.isTimeout()) {
                    value.markTimeout();
                }
            }
        }
    }

    /**
     * 发送请求，保存请求信息
     *
     * @param request 网络请求
     * @return 响应
     */
    public NetPacket sendRequest(NetPacket request) {
        NetSyncRequestPromise promise = new NetSyncRequestPromise(request);
        promises.put(request.getSequence(), promise);
        socketChannel.writeAndFlush(request);
        if (log.isDebugEnabled()) {
            log.debug("Send requests and synchronize waiting results：[request={}, sequence={}]",
                    PacketType.getEnum(request.getType()).getDescription(), request.getSequence());
        }
        return promise.getResult();
    }

    /**
     * 收到响应
     *
     * @param response 响应
     * @return 是否处理消息
     */
    public boolean onResponse(NetPacket response) {
        long sequence = response.getSequence();
        NetSyncRequestPromise requestPromise = promises.get(sequence);
        if (requestPromise != null) {
            NetSyncRequestPromise request = promises.remove(sequence);
            if (request != null) {
                request.setResult(response);
                return true;
            }
        }
        return false;
    }
}
