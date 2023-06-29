package com.susu.proxy.core.netty;

import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.netty.msg.NetPacketDecoder;
import com.susu.proxy.core.netty.msg.NetPacketEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Description: 一个简单的不做任何处理的消息处理器</p>
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@ChannelHandler.Sharable
public class BaseChannelHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 添加其他处理器的数组
     */
    private List<AbstractChannelHandler> handlerList = new LinkedList<>();

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new NetPacketDecoder(Constants.MAX_BYTES),
                new LengthFieldPrepender(3),
                new NetPacketEncoder()
        );
        for (AbstractChannelHandler handler : handlerList) {
            ch.pipeline().addLast(handler);
        }
    }

    public void addHandler(AbstractChannelHandler handler) {
        handlerList.add(handler);
    }
}
