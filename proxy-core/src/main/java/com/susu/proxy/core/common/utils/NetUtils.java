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

    public static String getChannelId(ChannelHandlerContext channel) {
        return channel.channel().id().asLongText().replaceAll("-", "");
    }

    public static int getChannelPort(ChannelHandlerContext channel) {
        return ((InetSocketAddress) channel.channel().localAddress()).getPort();
    }

}