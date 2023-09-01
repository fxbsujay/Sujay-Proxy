package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.eum.ProxyStateType;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.core.common.entity.PortMapping;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: Port Instantiation Strategy</p>
 * <p>Description: 端口实例化策略 </p>
 *
 * @author sujay
 * @since 15:20 2022/07/26
 * @version 1.0 JDK1.8
 */
@Slf4j
public class PortInstantiationStrategy extends AbstractProxyServerFactory {

    /**
     * 客户端管理器
     */
    private final MasterClientManager clientManager;

    /**
     * 端口映射池
     * key:     服务端端口
     */
    private final Map<Integer, PortMapping> pool = new ConcurrentHashMap<>();

    public PortInstantiationStrategy(MasterClientManager clientManager, TaskScheduler scheduler) {
        super(scheduler);
        this.clientManager = clientManager;
    }

    /**
     * 创建端口映射
     */
    public boolean createMapping(PortMapping mapping) {

        Integer serverPort = mapping.getServerPort();
        if (isExist(serverPort)) {
            return false;
        }

        if (mapping.getProtocol() == null) {
            return false;
        }

        boolean bind;
        try {
            bind = bind(mapping.getServerPort());
        } catch (InterruptedException e) {
            log.error("Port creation failed, {}", e.getMessage());
            return false;
        }

        if (bind) {
            pool.put(serverPort, mapping);
        }

        return bind;
    }

    /**
     * 删除端口映射
     */
    public PortMapping removeMapping(int port) {
        close(port);
        return pool.remove(port);
    }


    /**
     * 更新代理状态
     *
     * @param ip    代理客户端ip
     * @param ports 代理客户端端口
     * @param state 代理状态
     */
    public void setConnectState(String ip, List<Integer> ports, ProxyStateType state) {
        for (Map.Entry<Integer, PortMapping> entry : pool.entrySet()) {
            PortMapping mapping = entry.getValue();
            if (mapping.getClientIp().equals(ip) && (ports.isEmpty() || ports.contains(mapping.getClientPort()))) {
                mapping.setState(state);
                pool.put(entry.getKey(), mapping);
                return;
            }
        }
    }

    public void setConnectState(String ip, ProxyStateType state) {
        setConnectState(ip, new ArrayList<>(), state);
    }

    public void setConnectState(String ip, Integer port, ProxyStateType state) {
        setConnectState(ip, Collections.singletonList(port), state);
    }

    @Override
    public boolean isExist(int port) {
        return pool.containsKey(port);
    }

    @Override
    public List<Integer> getAllPort() {
        return new ArrayList<>(pool.keySet());
    }

    @Override
    public ProtocolType getProtocol(int port) {
        PortMapping portMapping = getMapping(port);
        if (portMapping == null) {
            return null;
        }
        return portMapping.getProtocol();
    }

    public PortMapping getMapping(int port) {
        return pool.get(port);
    }

    @Override
    public List<PortMapping> getAllMapping() {
        return new ArrayList<>(pool.values());
    }

    @Override
    protected void channelReadInternal(int port, byte[] bytes) {
        PortMapping mapping = getMapping(port);

        if(mapping == null || !clientManager.isExist(mapping.getClientIp())) {
            return;
        }

        NetPacket packet = NetPacket.buildPacket(bytes, PacketType.TRANSFER_NETWORK_PACKET);
        packet.setAddress(mapping.getClientIp() + ":" + mapping.getClientPort());

        try {
            clientManager.send(mapping.getClientIp(), packet);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void invokeVisitorConnectListener(ChannelHandlerContext ctx, boolean isConnected) {
    }
}
