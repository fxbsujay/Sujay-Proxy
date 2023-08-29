package com.susu.proxy.client.proxy;


import com.susu.proxy.client.transmit.MasterClient;
import com.susu.proxy.core.common.entity.PortMapping;
import com.susu.proxy.core.common.utils.NetUtils;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

    /**
     *  EventLoopGroup
     */
    private NioEventLoopGroup worker;

    private Bootstrap bootstrap;

    /**
     * 代理客户端
     */
    private final MasterClient masterClient;

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
        this.worker = new NioEventLoopGroup();

        this.bootstrap = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProxyChannelHandle(masterClient));
                    }
                });

        masterClient.setProxyManager(this);
    }

    public void createProxy(PortMapping mapping) {
        String address = mapping.getClientIp() + ":" + mapping.getClientPort();
        if (!mappings.containsKey(address)) {
            mappings.put(address, mapping);
        }

        connect(mapping.getClientIp(), mapping.getClientPort());
    }

    public void connect(String host, int port) {
        connect(host, port, 0);
    }

    /**
     * 客户端连接
     *
     * @param host  服务地址
     * @param port  服务端口
     * @param delay 任务延时毫秒数
     */
    private void connect(String host, int port, long delay) {

        String address = host + ":" + port;
        if (!mappings.containsKey(address)) {
            return;
        }

        taskScheduler.scheduleOnce("Real-Client",() -> {
            try {
                ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();


                if (future.isSuccess()) {
                    log.info("Successfully connected to the real server: {}", address);
                    channels.put(host + ":" + port, (SocketChannel) future.channel());
                }

                future.channel().closeFuture().sync();
                log.warn("Failed to connect to real server, ready to reconnect: {}", address);
            } catch (Exception e) {
                log.error("Real connection exception, ready to reconnect：[ex={}, inetSocketAddress: {}]", e.getMessage(), address);
            } finally {
                connect(host, port, 3000);
            }
        }, delay);
    }

    private void close(String address) {
        mappings.remove(address);
        SocketChannel channel = channels.remove(address);
        channel.close();
    }
}
