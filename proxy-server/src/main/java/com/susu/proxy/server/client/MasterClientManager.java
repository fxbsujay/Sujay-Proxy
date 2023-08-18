package com.susu.proxy.server.client;

import com.susu.proxy.core.common.model.RegisterRequest;
import com.susu.proxy.core.common.utils.DateUtils;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.common.utils.SnowFlakeUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.config.ServerConfig;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: Proxy Client Manager</p>
 * <p>Description: 客户端管理器 </p>
 *
 * @author sujay
 * @since 18:16 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class MasterClientManager {

    /**
     * 客户端实例
     *      Example:    {
     *                      key:    hostname
     *                      value:  clientInfo
     *                  }
     */
    private final Map<String, ClientInfo> clients = new ConcurrentHashMap<>();

    /**
     * 客户端通道
     *      Example:    {
     *                      key:    hostname
     *                      value:  clientInfo
     *                  }
     */
    private final Map<String, ChannelHandlerContext> channels = new ConcurrentHashMap<>();

    /**
     * 给注册进来的客户端分配id
     */
    private final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(1,1);

    public ClientInfo getClientByHost(String hostname) {
        return clients.get(hostname);
    }

    public List<ClientInfo> getClientList() {
        return new ArrayList<>(clients.values());
    }

    public MasterClientManager(TaskScheduler taskScheduler) {
        taskScheduler.schedule(
                "Client-Check",
                new ClientAliveMonitor(),
                ServerConfig.heartbeatCheckInterval,
                ServerConfig.heartbeatCheckInterval,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 客户端是否存在
     * @param hostname ip地址
     */
    public boolean isExist(String hostname) {
        return clients.containsKey(hostname) && channels.containsKey(hostname);
    }

    public ChannelHandlerContext getClientChannel(String hostname) {
        return channels.get(hostname);
    }

    /**
     * <p>Description: 客户端注册</p>
     * <p>Description: Client Register</p>
     * @param request 注册请求
     * @return 是否注册成功 【 true / false 】
     */
    public boolean register(RegisterRequest request, ChannelHandlerContext channel) {
        if (StringUtils.isBlank(request.getHostname())) {
            return false;
        }
        String hostname = request.getHostname();
        ClientInfo client = new ClientInfo(hostname, request.getName());
        client.setName(request.getName());
        client.setClientId(snowFlakeUtils.nextId());

        if (clients.get(hostname) != null) {
            log.info("Client reconnection : [name={}, hostname={}]", request.getName(), hostname);
        } else {
            log.info("Client registration : [name={}, hostname={}]", request.getName(), hostname);
        }


        clients.put(hostname, client);
        if (channel != null) channels.put(hostname, channel);
        return true;
    }

    /**
     * <p>Description: 客户端断线</p>
     * <p>Description: Client Disconnected</p>
     *
     * @param ctx 客户端通道
     */
    public void disconnected(ChannelHandlerContext ctx) {
        String ctxId = NetUtils.getChannelId(ctx);

        for (String hostname : channels.keySet()) {
            ChannelHandlerContext context = channels.get(hostname);
            String clientId = NetUtils.getChannelId(context);

            if (!clientId.equals(ctxId)) {
                continue;
            }

            channels.remove(hostname);
            ClientInfo clientInfo = clients.get(hostname);
            clientInfo.setStatus(ClientInfo.STATUS_DISCONNECT);
            ctx.fireChannelInactive();
            log.warn("Client disconnected : [name={}, hostname={}]", clientInfo.getName(), hostname);
            return;
        }
    }

    /**
     * <p>Description: 客户端心跳</p>
     * <p>Description: Client Heartbeat</p>
     *
     * @param hostname 客户端地址
     * @return 是否更新成功 【 true / false 】
     */
    public Boolean heartbeat(String hostname) {
        ClientInfo dataNode = clients.get(hostname);
        if (dataNode == null) {
            return false;
        }
        long latestHeartbeatTime = System.currentTimeMillis();
        dataNode.setLatestHeartbeatTime(latestHeartbeatTime);
        dataNode.setStatus(ClientInfo.STATUS_ACTIVE);
        return true;
    }

    /**
     * client 是否存活的监控线程
     */
    private class ClientAliveMonitor implements Runnable {
        @Override
        public void run() {
            Iterator<ClientInfo> iterator = clients.values().iterator();
            while (iterator.hasNext()) {
                ClientInfo next = iterator.next();
                long currentTimeMillis = System.currentTimeMillis();

                if (currentTimeMillis < next.getLatestHeartbeatTime() + ServerConfig.heartbeatOutTime) {
                    continue;
                }

                log.info("Client out time, remove client：[name={}, hostname={},latestHeartbeatTime={}]",
                        next.getName(), next.getStatus(), DateUtils.getTime(next.getLatestHeartbeatTime(), DateUtils.PATTERN_WHOLE));
                iterator.remove();
            }
        }
    }
}
