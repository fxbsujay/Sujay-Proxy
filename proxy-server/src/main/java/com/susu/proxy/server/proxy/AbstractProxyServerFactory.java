package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractProxyServerFactory implements ProxyServerFactory {

    protected final ProxyChannelHandle channelHandle;

    /**
     * 访客连接服务器
     */
    protected final NetServer proxyServer;

    /**
     * 访客群组 port -> channel
     */
    private final Map<Integer, List<ChannelHandlerContext>> visitorChannels = new ConcurrentHashMap<>();

    public AbstractProxyServerFactory(TaskScheduler scheduler) {
        this.channelHandle = new ProxyChannelHandle();
        initializeChannelHandle(channelHandle);
        this.proxyServer = new NetServer("proxy-server", scheduler);
        this.proxyServer.setBaseChannelHandler(this.channelHandle);
        this.proxyServer.startAsync();
    }

    @Override
    public boolean bind(int port) throws InterruptedException {
        proxyServer.bindAsync(port);
        visitorChannels.put(port, new ArrayList<>());
        return true;
    }

    @Override
    public boolean close(int port) {
        List<ChannelHandlerContext> channels = visitorChannels.remove(port);

        if (channels != null && !channels.isEmpty()) {
            channels.get(0).channel().parent().close();
            for (ChannelHandlerContext channel : channels) {
                channel.channel().close();
            }
        }

        log.info("Visitor proxy channels are all closed : {}", port);
        return true;
    }

    /**
     * 初始化代理客户端通道处理器
     */
    private  void initializeChannelHandle(ProxyChannelHandle channelHandle) {
        channelHandle.addHandler(new SimpleChannelInboundHandler<ByteBuf>() {

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                log.error("VisitorChannelHandler exception caught：", cause);
            }

            /**
             * 访客连接成功
             */
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                int port = NetUtils.getChannelPort(ctx);
                if (isExist(port)) {
                    ctx.channel().close();
                    close(port);
                    return;
                }

                List<ChannelHandlerContext> channels = visitorChannels.get(port);
                if (channels == null) {
                    visitorChannels.put(port, Collections.singletonList(ctx));
                } else {
                    channels.add(ctx);
                }

                log.info("Visitor channel is connected: {}", ctx.channel());

                invokeVisitorConnectListener(ctx, true);
            }

            /***
             * 访客断开连接
             */
            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                int port = NetUtils.getChannelPort(ctx);
                List<ChannelHandlerContext> channels = visitorChannels.get(port);
                if (channels != null) {
                    channels = channels.stream().filter(item -> !NetUtils.getChannelId(item).equals(NetUtils.getChannelId(ctx))).collect(Collectors.toList());
                    visitorChannels.put(port, channels);
                }
                log.debug("Visitor channel is disconnected！{}", ctx.channel());
                invokeVisitorConnectListener(ctx, false);
            }

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
                int port = NetUtils.getChannelPort(ctx);
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                channelReadInternal(port, bytes);
            }
        });
    };

    /**
     * 消息处理
     *
     * @param port  端口
     * @param bytes 数据
     */
    protected abstract void channelReadInternal(int port, byte[] bytes);

    /**
     * 访客连接监听
     *
     * @param ctx           访客
     * @param isConnected   是连接还是断开
     */
    protected abstract void invokeVisitorConnectListener(ChannelHandlerContext ctx, boolean isConnected);
}
