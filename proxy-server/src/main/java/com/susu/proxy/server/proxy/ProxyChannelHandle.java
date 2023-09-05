package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.netty.BaseChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 访客消息处理器 </p>
 *
 * @author fxbsujay@gmail.com
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ProxyChannelHandle extends BaseChannelHandler {

    private final ProxyServerFactory serverFactory;

    public ProxyChannelHandle(ProxyServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    @Override
    protected void initChannel(SocketChannel ch) {

        ProtocolType protocol = serverFactory.getProtocol(ch.localAddress().getPort());
        if (protocol == ProtocolType.HTTP) {
            ch.pipeline().addLast(new HttpObjectAggregator(65536));
            ch.pipeline().addLast(new ChunkedWriteHandler());
        }

        for (ChannelInboundHandlerAdapter handler : handlerList) {
            ch.pipeline().addLast(handler);
        }
    }

    public void addHandler(ChannelInboundHandlerAdapter handler) {
        handlerList.add(handler);
    }
}
