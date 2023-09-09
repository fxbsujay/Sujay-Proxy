package com.susu.proxy.server.proxy;

import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.netty.listener.NetBindingListener;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
    protected final Map<Integer, List<ChannelHandlerContext>> visitorChannels = new ConcurrentHashMap<>();

    /**
     * 端口绑定监听器
     */
    private NetBindingListener bindingListener;

    public AbstractProxyServerFactory(TaskScheduler scheduler) {
        this.channelHandle = new ProxyChannelHandle(this);
        this.channelHandle.addHandler(new ProxySimpleChannelHandler());
        this.proxyServer = new NetServer("proxy-server", scheduler);
        this.proxyServer.setBaseChannelHandler(this.channelHandle);
        this.proxyServer.startAsync();
    }

    @Override
    public void bind(int port) {
        proxyServer.bindAsync(Collections.singletonList(port), bindingListener);
        setAllVisitorChannel(port,new ArrayList<>());
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

    public void close(String visitor) {

        for (Map.Entry<Integer, List<ChannelHandlerContext>> entry : visitorChannels.entrySet()) {
            List<ChannelHandlerContext> contexts = entry.getValue();

            for (ChannelHandlerContext context : contexts) {
                if (NetUtils.getChannelId(context).equals(visitor)) {
                    entry.getValue().remove(context);
                    context.channel().close();
                    return;
                }
            }
        }
    }

    public void send(String visitor, ByteBuf buf) {
        List<ChannelHandlerContext> contexts = getVisitorChannel(visitor);
        if (contexts != null && !contexts.isEmpty()) {
            for (ChannelHandlerContext ctx : contexts) {
                ctx.channel().writeAndFlush(buf);
            }
        }
    }

    public void setBindingListener(NetBindingListener listener) {
        this.bindingListener = listener;
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
     * 是否存在连接
     *
     * @param port        服务端代理端口
     */
    public boolean isConnectionExists(int port) {
        List<ChannelHandlerContext> contexts = visitorChannels.get(port);
        return contexts != null && contexts.size() > 0;
    }

    public List<ChannelHandlerContext> getVisitorChannel(String channelId) {
        List<ChannelHandlerContext> result = new ArrayList<>();
        for (List<ChannelHandlerContext> contexts : visitorChannels.values()) {
            if (contexts.isEmpty()) {
                continue;
            }
            for (ChannelHandlerContext context : contexts) {
                if (channelId.equals(NetUtils.getChannelId(context))) {
                    result.add(context);
                }
            }
        }
        return result;
    }

    public Integer getVisitorPort(String channelId) {
        for (Map.Entry<Integer, List<ChannelHandlerContext>> entry : visitorChannels.entrySet()) {
            List<ChannelHandlerContext> contexts = entry.getValue();
            if (contexts.isEmpty()) {
                continue;
            }
            for (ChannelHandlerContext context : contexts) {
                if (channelId.equals(NetUtils.getChannelId(context))) {
                    return entry.getKey();
                }
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
    protected abstract void channelReadInternal(String visitorId, int port, byte[] bytes);

    /**
     * 访客连接监听
     *
     * @param visitorId           访客
     * @param isConnected   是连接还是断开
     */

    protected abstract void invokeVisitorConnectListener(String visitorId, int port, boolean isConnected);

    @ChannelHandler.Sharable
    public class ProxySimpleChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

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
            invokeVisitorConnectListener(NetUtils.getChannelId(ctx), port, true);
            ctx.fireChannelActive();
        }

        /***
         * 访客断开连接
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            int port = NetUtils.getChannelPort(ctx);
            String visitorId = NetUtils.getChannelId(ctx);
            List<ChannelHandlerContext> channels = visitorChannels.get(port);

            if (channels != null && channels.contains(ctx)) {
                channels = channels.stream().filter(item -> !NetUtils.getChannelId(item).equals(visitorId)).collect(Collectors.toList());
                setAllVisitorChannel(port, channels);
                invokeVisitorConnectListener(visitorId, port,false);
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {

            int port = NetUtils.getChannelPort(ctx);
            if (!isExist(port)) {
                ctx.channel().close();
                close(port);
                return;
            }

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            channelReadInternal(NetUtils.getChannelId(ctx), port, bytes);
        }
    }
}
