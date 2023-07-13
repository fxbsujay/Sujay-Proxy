package com.susu.proxy.core.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>Description: Constants</p>
 * <p>Description: 常量</p>
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
public class Constants {

    /**
     * 配置文件名称
     */
    public static final String CONFIG_FILE_NAME = "/application.yaml";

    /**
     * 系统默认编号
     */
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * 服务心跳心跳间隔
     */
    public static final int HEARTBEAT_INTERVAL = 30000;

    /**
     * Netty 最大传输字节数
     */
    public static final int MAX_BYTES = 10 * 1024 * 1024;

    /**
     * map初始化容量
     */
    public static final int MAP_SIZE = 32;

    /**
     * 分块传输，每一块的大小
     */
    public static final int CHUNKED_SIZE = (int) (MAX_BYTES * 0.5F);

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
