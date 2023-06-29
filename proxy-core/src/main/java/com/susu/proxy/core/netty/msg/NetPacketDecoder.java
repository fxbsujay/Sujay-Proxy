package com.susu.proxy.core.netty.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 消息编码器</p>
 *
 *  +--------+-------------------------------+---------------+-----------------------------+
 *  | HeaderLength | Actual Header (18byte)  | ContentLength | Actual Content (25byte)     |
 *  | 0x0012       | Header Serialization    | 0x0019        | Body  Serialization         |
 *  +--------------+-------------------------+---------------+-----------------------------+
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetPacketDecoder extends LengthFieldBasedFrameDecoder {

    public NetPacketDecoder(int maxFrameLength) {
        super(maxFrameLength, 0, 3, 0, 3);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, byteBuf);
        if (in != null) {
            try {
                return NetPacket.read(in);
            } finally {
                ReferenceCountUtil.release(in);
            }
        }
        return null;

    }
}
