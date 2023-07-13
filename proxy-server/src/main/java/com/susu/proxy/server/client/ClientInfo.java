package com.susu.proxy.server.client;

import lombok.Data;
import java.util.Objects;

/**
 * @author sujay
 * <p>Description: 客户端信息</p>
 * @version 13:14 2023/07/13
 */
@Data
public class ClientInfo {

    /**
     * 心跳检测初始化
     */
    public static final int STATUS_INIT = 1;

    /**
     * 心跳检测就绪
     */
    public static final int STATUS_READY = 2;

    /**
     * 客户端Id
     */
    private long clientId;

    /**
     * 客户端Name
     */
    private String name;

    /**
     * 节点地址
     */
    private String hostname;

    /**
     * 客户端状态
     */
    private int status;

    /**
     * 最近一次心跳时间
     */
    private long latestHeartbeatTime;

    public ClientInfo(String hostname, String name) {
        this.name = name;
        this.hostname = hostname;
        this.latestHeartbeatTime = System.currentTimeMillis();
        this.status = STATUS_INIT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientInfo that = (ClientInfo) o;
        return clientId == that.clientId && Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, clientId);
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "hostname='" + hostname + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}
