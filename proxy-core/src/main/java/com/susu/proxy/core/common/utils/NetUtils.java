package com.susu.proxy.core.common.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * <p>Netty 工具类</p>
 *
 * @author sujay
 * @since 21:44 2022/08/16
 * @version 1.0 JDK1.8 <br/>
 */
public class NetUtils {

    public static String getChannelId(ChannelHandlerContext ctx) {
        return getChannelId(ctx.channel());
    }

    public static String getChannelId(Channel channel) {
        return channel.id().asLongText().replaceAll("-", "");
    }

    public static int getChannelPort(ChannelHandlerContext channel) {
        return getChannelPort(channel.channel());
    }

    public static int getChannelPort(Channel channel) {
        return ((InetSocketAddress)channel.localAddress()).getPort();
    }
}
