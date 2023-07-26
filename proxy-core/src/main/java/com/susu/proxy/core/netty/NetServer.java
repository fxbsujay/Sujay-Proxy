package com.susu.proxy.core.netty;

import com.susu.proxy.core.task.TaskScheduler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Description: Server NetWork</p>
 * <p>Description: Netty 的 服务端实现 网络服务</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetServer {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 任务调度器
     */
    private TaskScheduler taskScheduler;

    /**
     * 消息管理器
     */
    private BaseChannelHandler baseChannelHandler;

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    private ServerBootstrap bootstrap;

    public NetServer(String name) {
        this(name, null);
    }

    /**
     * @param name 启动的节点名称
     */
    public NetServer(String name, TaskScheduler taskScheduler) {
        this.name = name;
        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();
        this.baseChannelHandler = new BaseChannelHandler();
        this.taskScheduler = taskScheduler;
    }

    /**
     * 异步绑定端口
     * <p>Description: start server </p>
     *
     * @param port 端口
     */
    public void startAsync(Integer... port) {
        taskScheduler.scheduleOnce(name, () -> {
            try {
                start(Arrays.asList(port));
            } catch (InterruptedException e) {
                log.info("NetServer internalBind is Interrupted !!");
            }
        }, 0);
    }

    /**
     * 启动服务
     * <p>Description: start server </p>
     *
     * @param ports 端口
     * @exception InterruptedException 绑定端口异常
     */
    public void start(int ports) throws InterruptedException {
        start(Collections.singletonList(ports));
    }

    public void start() throws InterruptedException {
        start(Collections.emptyList());
    }

    /**
     * 启动服务
     * <p>Description: start server </p>
     *
     * @param ports 端口
     * @exception InterruptedException 绑定端口异常
     */
    public void start(List<Integer> ports) throws InterruptedException {
         bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(baseChannelHandler);

        log.info("NetServer started : {}", name);

        if (ports == null || ports.isEmpty()) {
            return;
        }

        try {
            bindSync(ports);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 绑定端口
     * <p>Description: bind port </p>
     *
     * @param ports 端口号
     * @throws InterruptedException 绑定端口异常
     */
    public void bindSync(List<Integer> ports) throws InterruptedException {
        List<ChannelFuture> channelFeature = new ArrayList<>();
        for (Integer port : ports) {
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("NetServer bind port : {}", port);
            channelFeature.add(future);
        }
        for (ChannelFuture future : channelFeature) {
            future.channel().closeFuture().addListener((ChannelFutureListener) future1 -> future1.channel().close());
        }
        for (ChannelFuture future : channelFeature) {
            future.channel().closeFuture().sync();
        }
    }

    public void bindSync(int port) throws InterruptedException {
        bindSync(Collections.singletonList(port));
    }

    /**
     * 添加自定义的handler
     */
    public void addHandler(AbstractChannelHandler handlers) {
        baseChannelHandler.addHandler(handlers);
    }

    public void setBaseChannelHandler(BaseChannelHandler baseChannelHandler) {
        this.baseChannelHandler = baseChannelHandler;
    }

    /**
     * 关闭服务端
     * <p>Description: shutdown server </p>
     */
    public void shutdown() {
        log.info("Shutdown NetServer : {}", name);
        if (boss != null && worker != null) {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
