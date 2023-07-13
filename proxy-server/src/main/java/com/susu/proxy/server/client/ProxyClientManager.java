package com.susu.proxy.server.client;


import com.susu.proxy.core.common.model.RegisterRequest;
import com.susu.proxy.core.common.utils.SnowFlakeUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: Proxy Client Manager</p>
 * <p>Description: 客户端管理器 </p>
 *
 * @author sujay
 * @since 18:16 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ProxyClientManager {


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
        if (clients.get(hostname) != null) {
            log.error("Registration failed, client side is registered : [hostname:{}]", hostname);
            return false;
        }

        ClientInfo client = new ClientInfo(hostname, request.getName());
        client.setName(request.getName());
        client.setClientId(snowFlakeUtils.nextId());
        log.info("Client register request : [hostname:{}]",hostname);

        clients.put(request.getHostname(),client);
        if (channel != null) channels.put(hostname,channel);
        return true;
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
        return true;
    }
}
