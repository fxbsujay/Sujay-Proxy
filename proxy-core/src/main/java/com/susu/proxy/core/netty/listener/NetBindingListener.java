package com.susu.proxy.core.netty.listener;

public interface NetBindingListener {

    void onBindStatusChanged(int port, boolean isBinding) throws Exception;
}
