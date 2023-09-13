package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.LocalStorage;
import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.model.VisitorConnectingRequest;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.core.common.entity.PortMapping;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
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

    private final String path =  System.getProperty("user.dir") + File.separator + "data" + File.separator + "mapping.info";

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
        loadReadyMappings();
        Runtime.getRuntime().addShutdownHook(new Thread(this::loadWriteMappings));
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

        pool.put(serverPort, mapping);
        bind(mapping.getServerPort());
        loadWriteMappings();
        return true;
    }

    /**
     * 删除端口映射
     */
    public PortMapping removeMapping(int port) {
        close(port);
        PortMapping mapping = pool.remove(port);
        loadWriteMappings();
        return mapping;
    }

    private void loadReadyMappings() {
        pool.clear();
        List<PortMapping> mappingsJson = LocalStorage.loadReady(path, PortMapping.class);
        if (mappingsJson == null || mappingsJson.isEmpty()) {
            return;
        }
        for (PortMapping mapping : mappingsJson) {
            pool.put(mapping.getServerPort(), mapping);
            bind(mapping.getServerPort());
        }
    }

    private void loadWriteMappings() {
        LocalStorage.loadWrite(path, pool.values());
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
    public void serverStatusListener(int port, boolean status) {
        PortMapping mapping = pool.get(port);
        if (mapping != null) {
            mapping.setBinding(status);
        }

        if (!status) {
            log.error("Failed to create proxy server with port {}", port);
        }
    }

    @Override
    protected void channelReadInternal(String visitor, int port, byte[] bytes) {

        PortMapping mapping = getMapping(port);

        if(mapping == null || !clientManager.isExist(mapping.getClientIp())) {
            return;
        }

        NetPacket packet = NetPacket.buildPacket(bytes, PacketType.TRANSFER_NETWORK_PACKET);
        packet.setVisitorId(visitor);

        try {
            clientManager.send(mapping.getClientIp(), packet);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void invokeVisitorConnectListener(String visitorId, int port, boolean isConnected) {

        byte[] bytes;
        PacketType packetType;
        if (isConnected) {
            VisitorConnectingRequest connectingRequest = VisitorConnectingRequest
                    .newBuilder()
                    .setServerPort(port)
                    .build();
            bytes = connectingRequest.toByteArray();
            packetType = PacketType.SERVER_VISITOR_CONNECTING;
        } else {
            bytes = new byte[0];
            packetType = PacketType.CONNECTION_CLOSURE_NOTIFICATION;
        }

        NetPacket packet = NetPacket.buildPacket(bytes, packetType);
        packet.setVisitorId(visitorId);
        PortMapping mapping = getMapping(port);
        log.info("Listening on visitor connection: [real-server: {}:{}, message: {}, visitorId: {} ]", mapping.getClientIp(), mapping.getClientPort(), packetType, visitorId);

        try {
            clientManager.send(mapping.getClientIp(), packet);
        } catch (InterruptedException e) {
            log.error("Notifying the client side of the connection status failed because: {}", e.getMessage());
        }
    }
}
