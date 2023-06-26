package com.susu.proxy.core.netty.msg;

import com.susu.proxy.core.common.eum.PacketType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * <p>Description: 统一的网络数据包</p>
 * <p>Description: Unified network packets</p>
 *
 * @author sujay
 * @version 11:36 2022/7/6
 */
@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class NetPacket {

    private int type;

    private long sequence;

    private int code;

    private String data;

    public NetPacket(int type, String data) {
        this.type = type;
        this.data = data;
    }

    /**
     * 一个静态的构造器
     *
     * @param data          请求体
     * @param packetType    请求体类型
     * @return              数据包
     */
    public static NetPacket buildPacket(String data, PacketType packetType){
        NetPacketBuilder builder = NetPacket.builder();
        builder.data = data;
        builder.code = 200;
        NetPacket nettyPacket = builder.build();
        nettyPacket.setType(packetType.value);
        return nettyPacket;
    }

    /**
     * 复制
     */
    public static NetPacket copy(NetPacket nettyPacket) {
        return new NetPacket(nettyPacket.type, nettyPacket.data);
    }

    /**
     * <p>Description: 写入数据</p>
     *
     * @param out 输出
     */
    public void write(ByteBuf out) {
        out.writeInt(type);
        out.writeInt(code);
        out.writeLong(sequence);
        byte[] bytes = data.getBytes();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    /**
     * <p>Description: 读取ByteBuf，转成NetPacket</p>
     *
     * @param in 输入
     * @return 数据包
     */
    public static NetPacket read(ByteBuf in) {
        int type = in.readInt();
        int code = in.readInt();
        long sequence = in.readLong();
        int bodyLength = in.readInt();
        byte[] bodyBytes = new byte[bodyLength];
        in.readBytes(bodyBytes);
        return NetPacket.builder()
                .code(code)
                .type(type)
                .sequence(sequence)
                .data(new String(bodyBytes))
                .build();
    }
}
