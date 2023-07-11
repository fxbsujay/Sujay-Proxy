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