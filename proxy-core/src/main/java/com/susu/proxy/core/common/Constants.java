package com.susu.proxy.core.common;

/**
 * <p>Description: Constants</p>
 * <p>Description: 常量</p>
 * @author sujay
 * @version 15:56 2022/7/6
 */
public class Constants {

    /**
     * Netty 最大传输字节数
     */
    public static final int MAX_BYTES = 10 * 1024 * 1024;

    /**
     * map初始化容量
     */
    public static final int MAP_SIZE = 32;

    /**
     * 请求处理器核心线程数
     */
    public static final int HANDLE_THREAD_EXECUTOR_CORE_SIZE = 200;

    /**
     * 请求处理器最大线程数
     */
    public static final int HANDLE_THREAD_EXECUTOR_CORE_SIZE_MAX = 200;

    /**
     * 请求处理器队列数
     */
    public static final int HANDLE_THREAD_EXECUTOR_QUEUE_SIZE_MAX = 2000;

}
