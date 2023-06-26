package com.susu.proxy.core.common.eum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请求类型
 *
 * @author sujay
 */
@Getter
@AllArgsConstructor
public enum PacketType {

    /**
     * 请求类型
     */
    EMPTY(1001, "空的的包类型"),
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