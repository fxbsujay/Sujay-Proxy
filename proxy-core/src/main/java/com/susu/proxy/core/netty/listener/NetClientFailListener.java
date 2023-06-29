package com.susu.proxy.core.netty.listener;


/**
 * <p>Description: 客户端连接失败监听器</p>
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
public interface NetClientFailListener {

    /**
     * 连接是失败触发该方法
     */
    void onConnectFail();
}
