package com.susu.proxy.client.proxy;

import com.susu.proxy.core.common.entity.PortMapping;
import com.susu.proxy.core.common.eum.ProxyStateType;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: 代理管理器 </p>
 *
 * @author sujay
 * @since 17:37 2023/08/28
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ProxyManager {

    private final Bootstrap bootstrap;

    /**
     * 代理客户端
     */
    private final MasterClient masterClient;

    /**
     * 线程池
     */
    private final TaskScheduler taskScheduler;

    /**
     * 真实服务连接通道
     * 访客ID -> SocketChannel
     */
    private final Map<String, SocketChannel> channels = new ConcurrentHashMap<>();

    /**
     * 访客记录
     * 服务端代理端口 -> 访客ID
     */
    private final Map<Integer, List<String>> visitors = new ConcurrentHashMap<>();

    /**
     * 端口映射池
     * key:     服务端代理端口
     * value:   代理信息
     */
    private final Map<Integer, PortMapping> pool = new ConcurrentHashMap<>();

    public ProxyManager(MasterClient masterClient, TaskScheduler taskScheduler) {
        this.masterClient = masterClient;
        this.taskScheduler = taskScheduler;

        this.bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProxyChannelHandle());
                    }
                });

        masterClient.setProxyManager(this);
    }

    /**
     * <p>Description: 创建代理</p>
     * <p>Description: Create proxy</p>
     *
     * @param mapping Port mapping information
     */
    public void create(PortMapping mapping) {
        Integer port = mapping.getServerPort();
        if (!pool.containsKey(mapping.getServerPort())) {
            pool.put(port, mapping);
            visitors.put(port, new ArrayList<>());
        }
    }

    /**
     * <p>Description: 代理客户端连接真实服务端</p>
     * <p>Description: The client side connects to the real server</p>
     *
     * @param visitorId   访客ID
     * @param serverPort  服务端端口
     */
    private void connect(final String visitorId, final int serverPort) {

        if (!pool.containsKey(serverPort)) {
            return;
        }

        PortMapping mapping = pool.get(serverPort);

        String ip = mapping.getClientIp();
        Integer port = mapping.getClientPort();

        taskScheduler.scheduleOnce("Real-Client",() -> {
            try {
                ChannelFuture future = bootstrap.connect(new InetSocketAddress(mapping.getClientIp(), mapping.getClientPort())).sync();

                if (future.isSuccess()) {
                    setConnectState(serverPort, ProxyStateType.RUNNING);
                    channels.put(visitorId, (SocketChannel) future.channel());
                    log.info("Successfully connected to the real server: {}:{}", ip, port);
                }

                future.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("Real connection exception, ready to reconnect：[ex={}, inetSocketAddress: {}:{}]", e.getMessage(), ip, port);
            } finally {
                channels.remove(visitorId);
                setConnectState(serverPort, ProxyStateType.CLOSE);
                log.info("The real server is disconnected and the proxy service will be temporarily shut down: {}:{}", ip, port);
            }
        });
    }

    /**
     * <p>Description: 同步代理信息</p>
     * <p>Description: Sync port proxy information</p>
     *
     * @param proxies 目前所有的代理信息
     */
    public void syncProxies(List<PortMapping> proxies) {
        pool.clear();
        for (PortMapping mapping : proxies) {
            create(mapping);
        }
    }

    /**
     * <p>Description: 删除代理</p>
     * <p>Description: Close proxy</p>
     *
     * @param port Proxy server port. Example: 8848
     */
    public void remove(Integer port) {

        pool.remove(port);

        List<String> visitorsIds = visitors.remove(port);
        if (visitorsIds == null || visitorsIds.isEmpty()) {
            return;
        }

        for (String visitorsId : visitorsIds) {
            SocketChannel socketChannel = channels.remove(visitorsId);
            if (socketChannel != null) {
                socketChannel.close();
            }
        }

    }

    public void send(String visitorId, ByteBuf buf) {
        SocketChannel channel = channels.get(visitorId);
        if (channel != null) {
            channel.writeAndFlush(buf);
        }
    }

    /**
     * <p>Description: 更新客户端连接的状态并告知服务端</p>
     * <p>Description: Update client side connection status and inform server level</p>
     *
     * @param serverPort  服务端代理端口
     * @param state 状态
     */
    private void setConnectState(int serverPort, ProxyStateType state) {
        if (!pool.containsKey(serverPort)) {
            return;
        }
        try {
            masterClient.reportConnectFuture(serverPort, state);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PortMapping getMappingByServerPort(Integer port) {
        return pool.get(port);
    }

    /**
     * <p>Description: 根据通道上下文获取对应的访客Id</p>
     * <p>Description: Get the corresponding guest Id according to the channel context</p>
     */
    private String getVisitorId(ChannelHandlerContext ctx) {
        for (Map.Entry<String, SocketChannel> entry : channels.entrySet()) {
            SocketChannel channel = entry.getValue();
            if (NetUtils.getChannelId(ctx).equals(NetUtils.getChannelId(channel))) {
                return entry.getKey();
            }
        }
        return null;
    }

    private class ProxyChannelHandle extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
            String visitorId = getVisitorId(ctx);

            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.writeBytes(byteBuf);

            masterClient.forwardMessageRequest(visitorId, bytes);
        }
    }
}
