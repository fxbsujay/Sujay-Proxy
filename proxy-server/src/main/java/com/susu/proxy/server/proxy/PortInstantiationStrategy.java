package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.server.entity.PortMapping;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public void removeMapping(int port) {
        pool.remove(port);
        close(port);
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
        PortMapping portMapping = pool.get(port);
        if (portMapping == null) {
            return null;
        }
        return portMapping.getProtocol();
    }

    @Override
    public List<PortMapping> getAllMapping() {
        return new ArrayList<>(pool.values());
    }

    @Override
    protected void channelReadInternal(int port, byte[] bytes) {
        log.info("port: {}", port);
    }

    @Override
    protected void invokeVisitorConnectListener(ChannelHandlerContext ctx, boolean isConnected) {
        log.info("invokeVisitorConnectListener: {}", isConnected);
    }
}
