package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
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
        this.channelHandle = new ProxyChannelHandle(this);
        this.channelHandle.addHandler(new ProxySimpleChannelHandler());
        this.proxyServer = new NetServer("proxy-server", scheduler);
        this.proxyServer.setBaseChannelHandler(this.channelHandle);
        this.proxyServer.startAsync();
    }

    @Override
    public boolean bind(int port) throws InterruptedException {
        proxyServer.bindAsync(port);
        setAllVisitorChannel(port,new ArrayList<>());
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

    public List<ChannelHandlerContext> getAllVisitorChannel(int port) {
        return visitorChannels.get(port);
    }

    public void setAllVisitorChannel(int port, List<ChannelHandlerContext> channels) {
        visitorChannels.put(port, channels);
    }

    /**
     * 获取访客通道
     *
     * @param port        服务端代理端口
     * @param channelId   访客通道Id
     */
    public ChannelHandlerContext getVisitorChannel(int port, String channelId) {
        List<ChannelHandlerContext> channels = getAllVisitorChannel(port);
        if (channels == null) {
            return null;
        }
        for (ChannelHandlerContext channel : channels) {
            if (NetUtils.getChannelId(channel).equals(channelId)) {
                return channel;
            }
        }
        return null;
    }

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

    @ChannelHandler.Sharable
    public class ProxySimpleChannelHandler extends SimpleChannelInboundHandler<Object> {

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
            if (!isExist(port)) {
                ctx.channel().close();
                close(port);
                return;
            }

            List<ChannelHandlerContext> channels = visitorChannels.get(port);
            if (channels == null) {
                channels = new ArrayList<>();
                channels.add(ctx);
            } else {
                channels.add(ctx);
            }

            setAllVisitorChannel(port, channels);

            log.info("Visitor channel is connected: {}", port);

            invokeVisitorConnectListener(ctx, true);
            ctx.fireChannelActive();
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
                setAllVisitorChannel(port, channels);
            }
            log.info("Visitor channel is disconnected！{}", port);
            invokeVisitorConnectListener(ctx, false);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object data) {

            int port = NetUtils.getChannelPort(ctx);
            if (!isExist(port)) {
                ctx.channel().close();
                close(port);
                return;
            }

            ByteBuf buf;
            if (getProtocol(port) == ProtocolType.HTTP) {
                FullHttpRequest request = (FullHttpRequest) data;
                buf = request.content();
            } else {
                buf = (ByteBuf) data;
            }

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            channelReadInternal(port, bytes);
        }
    }
}
