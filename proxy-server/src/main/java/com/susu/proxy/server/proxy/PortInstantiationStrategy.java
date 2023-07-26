package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.server.entity.PortMapping;
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
public class PortInstantiationStrategy {

    /**
     * 客户端管理器
     */
    private final MasterClientManager clientManager;

    /**
     * 端口映射池
     * key:     服务端端口
     */
    private final Map<Integer, PortMapping> pool = new ConcurrentHashMap<>();

    public PortInstantiationStrategy(MasterClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public boolean createMapping(PortMapping mapping) {
        if (!clientManager.isExist(mapping.getClientIp())) {
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

        pool.put(serverPort, mapping);
        return true;
    }

    public boolean isExist(int port) {
        return pool.containsKey(port);
    }

}
