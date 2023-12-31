package com.susu.proxy.core.netty.msg;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>Description: 消息解码器</p>
 *
 *  +--------------+-------------------------+---------------+-----------------------------+
 *  | HeaderLength | Actual Header (18byte)  | ContentLength | Actual Content (25byte)     |
 *  | 0x0012       | Header Serialization    | 0x0019        | Body  Serialization         |
 *  +--------------+-------------------------+---------------+-----------------------------+
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
public class NetPacketEncoder extends MessageToByteEncoder<NetPacket> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NetPacket packet, ByteBuf out) throws Exception {
        packet.write(out);
    }

}
