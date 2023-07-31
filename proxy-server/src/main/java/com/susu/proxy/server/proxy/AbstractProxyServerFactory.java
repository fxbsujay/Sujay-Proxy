package com.susu.proxy.server.proxy;

import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
public abstract class AbstractProxyServerFactory implements ProxyServerFactory {

    protected final ProxyChannelHandle channelHandle;

    protected final NetServer proxyServer;

    private final TaskScheduler scheduler;

    /**
     * 访客群组 port -> channel
     */
    private final Map<Integer, List<ChannelHandlerContext>> visitorChannels = new ConcurrentHashMap<>();

    public AbstractProxyServerFactory(TaskScheduler scheduler) {
        this.scheduler = scheduler;
        this.channelHandle = new ProxyChannelHandle();
        initializeChannelHandle(channelHandle);
        this.proxyServer = new NetServer("proxy-server", this.scheduler);
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
        List<ChannelHandlerContext> channels = visitorChannels.get(port);
        for (ChannelHandlerContext channel : channels) {
            channel.channel().close();
            channel.channel().parent().close();
        }
        return true;
    }

    /**
     * 初始化代理客户端通道处理器
     */
    private  void initializeChannelHandle(ProxyChannelHandle channelHandle) {
        channelHandle.addHandler(new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
                ScheduledThreadPoolExecutor executor = scheduler.getExecutor();
                int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                executor.execute(() -> channelReadInternal(port, bytes));
            }
        });
    };

    protected abstract void channelReadInternal(int port, byte[] bytes);
}
