package com.susu.proxy.core.common.eum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请求类型
 *
 * @author sujay
 * @since 13:08 2022/4/28
 * @version 1.0 JDK1.8 <br/>
 */
@Getter
@AllArgsConstructor
public enum PacketType {

    /**
     * 请求类型
     */
    EMPTY(1001, "空的的包类型"),
    SERVICE_REGISTER(1002,"客户端注册"),
    SERVICE_HEART_BEAT(1003,"客户端心跳，服务端返回同步代理信息"),

    SERVER_CREATE_PROXY(2001,"向客户端发送创建代理的请求"),
    SERVER_SYNC_PROXY(2001, "同步代理"),
    SERVER_REMOVE_PROXY(2002,"向客户端发送删除代理的请求"),

    CLIENT_REPORT_FUTURE(3001,"当客户端连接真实服务端口或失败时上报客户端"),

    TRANSFER_NETWORK_PACKET(4001,"传输网络包")
    ;

    public int value;
    private String description;

    public static PacketType getEnum(int value) {
        for (PacketType packetType : values()) {
            if (packetType.getValue() == value) {
                return packetType;
            }
        }
        return EMPTY;
    }
}