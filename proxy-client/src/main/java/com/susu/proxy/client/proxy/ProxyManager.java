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
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
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
     * <p>Description: 删除代理</p>
     * <p>Description: Close proxy</p>
     *
     * @param port Proxy server port. Example: 8848
     */
    public void remove(Integer port) {
        pool.remove(port);
        clearVisitors(port);
    }

    /**
     * 向真实服务发送消息
     *
     * @param visitorId 访客ID
     * @param buf       消息内容
     */
    public void send(String visitorId, ByteBuf buf) {
        SocketChannel channel = channels.get(visitorId);
        if (channel != null) {
            channel.writeAndFlush(buf);
        }
    }


    /**
     * <p>Description: 代理客户端连接真实服务端</p>
     * <p>Description: The client side connects to the real server</p>
     *
     * @param visitorId   访客ID
     * @param serverPort  服务端端口
     */
    public void connect(String visitorId, int serverPort) {

        if (!pool.containsKey(serverPort) || StringUtils.isBlank(visitorId)) {
            return;
        }

        PortMapping mapping = pool.get(serverPort);

        if (mapping == null) {
            return;
        }

        String ip = mapping.getClientIp();
        Integer port = mapping.getClientPort();

        List<String> visitorIds = visitors.get(serverPort);
        if (visitorIds == null) {
            visitorIds = new ArrayList<>();
        }
        visitorIds.add(visitorId);
        visitors.put(serverPort, visitorIds);

        taskScheduler.scheduleOnce("Real-Client",() -> {
            try {
                ChannelFuture future = bootstrap.connect(new InetSocketAddress(mapping.getClientIp(), mapping.getClientPort())).sync();

                if (future.isSuccess()) {
                    channels.put(visitorId, (SocketChannel) future.channel());
                    log.info("Successfully connected to the real server: {}:{}", ip, port);
                }

                future.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("Real connection exception, ready to reconnect：[ex={}, inetSocketAddress: {}:{}]", e.getMessage(), ip, port);
            } finally {
                SocketChannel channel = channels.remove(visitorId);
                if (channel != null) {
                    masterClient.connectionClosureNotificationRequest(visitorId);
                }
                log.info("The real server is disconnected and the proxy service will be temporarily shut down: {}:{}", ip, port);
            }
        });
    }

    public void closeVisitor(String visitorId) {

        if (StringUtils.isBlank(visitorId)) {
            return;
        }

        for (List<String> visitorIds : visitors.values()) {
            if (visitorIds.isEmpty()) {
                continue;
            }
            for (String id : visitorIds) {
                if (id.equals(visitorId)) {
                    SocketChannel channel = channels.remove(visitorId);
                    if (channel != null) {
                        channel.close();
                    }
                    return;
                }
            }
        }
    }

    /**
     * <p>Description: 同步代理信息</p>
     * <p>Description: Sync port proxy information</p>
     *
     * @param proxies 目前所有的代理信息
     */
    public void syncProxies(List<PortMapping> proxies) {

        if (proxies.isEmpty()) {
            pool.clear();
            visitors.clear();
            if (!channels.isEmpty()) {
                for (SocketChannel channel : channels.values()) {
                    channel.close();
                }
                channels.clear();
            }
            return;
        }

        List<Integer> ports = new ArrayList<>();
        for (PortMapping mapping : proxies) {
            ports.add(mapping.getServerPort());
            PortMapping oldMapping = pool.get(mapping.getServerPort());

            if (oldMapping != null) {

                oldMapping.setClientIp(mapping.getClientIp());
                oldMapping.setClientPort(mapping.getClientPort());
                oldMapping.setProtocol(mapping.getProtocol());
                pool.put(mapping.getServerPort(), oldMapping);
                continue;
            }

            create(mapping);
        }

        for (Integer port : pool.keySet()) {
            if (!ports.contains(port)) {
                clearVisitors(port);
            }
        }
    }

    private void clearVisitors(Integer serverPort) {
        List<String> visitorIds = visitors.get(serverPort);
        if (visitorIds == null || visitorIds.isEmpty()) {
            return;
        }

        for (String visitorId : visitorIds) {
            SocketChannel channel = channels.remove(visitorId);
            if (channel != null) {
                channel.close();
            }
        }

        visitorIds.clear();
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

            masterClient.transferServerPacketRequest(visitorId, bytes);
        }
    }
}
