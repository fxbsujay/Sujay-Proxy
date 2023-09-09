package com.susu.proxy.core.common.entity;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.eum.ProxyStateType;
import lombok.Data;

import java.util.Objects;

/**
 * 端口映射
 */
@Data
public class PortMapping {

    /**
     * 代理协议  http or tcp
     */
    private ProtocolType protocol;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 代理客户端ip
     */
    private String clientIp;

    /**
     * 代理客户端端口
     */
    private Integer clientPort;

    /**
     * 是否已经绑定成功
     */
    private boolean binding;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortMapping mapping = (PortMapping) o;
        return protocol == mapping.protocol && Objects.equals(serverPort, mapping.serverPort) && Objects.equals(clientIp, mapping.clientIp) && Objects.equals(clientPort, mapping.clientPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, serverPort, clientIp, clientPort);
    }
}
