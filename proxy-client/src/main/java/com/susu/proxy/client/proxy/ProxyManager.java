package com.susu.proxy.client.proxy;


import com.susu.proxy.core.common.entity.PortMapping;
import com.susu.proxy.core.common.eum.ProxyStateType;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Mapping;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
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
     * localhost:3306 -> SocketChannel
     */
    private final Map<String, SocketChannel> channels = new ConcurrentHashMap<>();

    /**
     * 端口映射信息
     * localhost:3306 -> PortMapping
     */
    private final Map<String, PortMapping> mappings = new ConcurrentHashMap<>();

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
        String address = mapping.getClientIp() + ":" + mapping.getClientPort();
        if (!mappings.containsKey(address)) {
            mappings.put(address, mapping);
        }
        connect(mapping.getClientIp(), mapping.getClientPort());
    }

    private void connect(String host, int port) {
        connect(host, port, 0);
    }

    /**
     * <p>Description: 代理客户端连接真实服务端</p>
     * <p>Description: The client side connects to the real server</p>
     *
     * @param host  服务地址
     * @param port  服务端口
     * @param delay 任务延时毫秒数
     */
    private void connect(final String host, final int port, long delay) {

        String address = host + ":" + port;
        if (!mappings.containsKey(address) || channels.containsKey(address)) {
            return;
        }

        taskScheduler.scheduleOnce("Real-Client",() -> {
            try {
                ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
                if (future.isSuccess()) {
                    setConnectState(host, port, ProxyStateType.RUNNING);
                    log.info("Successfully connected to the real server: {}", address);
                    channels.put(host + ":" + port, (SocketChannel) future.channel());
                }

                future.channel().closeFuture().sync();
                log.info("The real server is disconnected and the proxy service will be temporarily shut down: {}", address);

                setConnectState(host, port, ProxyStateType.CLOSE);

            } catch (Exception e) {
                log.error("Real connection exception, ready to reconnect：[ex={}, inetSocketAddress: {}]", e.getMessage(), address);
            } finally {
                channels.remove(address);
                connect(host, port, 3000);
            }
        }, delay);
    }

    /**
     * <p>Description: 同步代理信息</p>
     * <p>Description: Sync port proxy information</p>
     *
     * @param proxies 目前所有的代理信息
     */
    public void syncProxies(List<PortMapping> proxies) {

        if (proxies.isEmpty()) {
            return;
        }

        List<String> addresses = new ArrayList<>();

        for (PortMapping mapping : proxies) {
            String address = mapping.getClientIp() + ":" + mapping.getClientPort();
            addresses.add(address);
            PortMapping oldMapping = mappings.get(address);

            if (oldMapping != null) {
                oldMapping.setServerPort(mapping.getServerPort());
                oldMapping.setProtocol(mapping.getProtocol());
                continue;
            }

            create(mapping);
        }

        for (String address : mappings.keySet()) {
            if (!addresses.contains(address)) {
                close(address);
            }
        }

        log.info("Sync port proxy information complete");
    }

    /**
     * <p>Description: 关闭代理</p>
     * <p>Description: Close proxy</p>
     *
     * @param address Real server address. Example: localhost:3305
     */
    public void close(String address) {
        mappings.remove(address);
        SocketChannel channel = channels.remove(address);
        if (channel != null) {
            channel.close();
        }

        log.info("Successfully close the agent: {}", address);
    }

    /**
     * <p>Description: 更新客户端连接的状态并告知服务端</p>
     * <p>Description: Update client side connection status and inform server level</p>
     *
     * @param host  真实服务的IP
     * @param port  真实服务的端口
     * @param state 状态
     */
    private void setConnectState(String host, int port, ProxyStateType state) {

        String address = host + ":" + port;
        PortMapping mapping = mappings.get(address);

        if (mapping != null) {
            mapping.setState(state);
            mappings.put(address, mapping);
        }

        try {
            masterClient.reportConnectFuture(host, port, state);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Description: 根据通道上下文获取对应的服务地址</p>
     * <p>Description: Get the corresponding service address according to the channel context</p>
     *
     * @return Example: localhost:3305
     */
    private String getSocketAddress(ChannelHandlerContext ctx) {
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
            String address = getSocketAddress(ctx);

            if (!mappings.containsKey(address)) {
                close(address);
                return;
            }

            log.info(byteBuf.toString());
        }
    }
}
