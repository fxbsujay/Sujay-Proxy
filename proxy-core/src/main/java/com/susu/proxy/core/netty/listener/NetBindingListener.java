package com.susu.proxy.core.netty.listener;


import io.netty.channel.Channel;

public interface NetBindingListener {

    void onBindStatusChanged(int port, Channel channel) throws Exception;
}
