package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.server.entity.PortMapping;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
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

    public boolean createMapping(PortMapping mapping) {
        if (clientManager.isExist(mapping.getClientIp())) {
            return false;
        }

        Integer serverPort = mapping.getServerPort();
        if (isExist(serverPort)) {
            return false;
        }

        ProtocolType protocol = ProtocolType.getEnum(mapping.getProtocol());
        if (protocol == null) {
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

    @Override
    public boolean close(int port) {
        pool.remove(port);
        return super.close(port);
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
    protected void channelReadInternal(int port, byte[] bytes) {

    }
}
