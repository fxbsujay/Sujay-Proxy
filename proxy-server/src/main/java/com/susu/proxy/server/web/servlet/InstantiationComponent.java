package com.susu.proxy.server.web.servlet;


/**
 * <p>Description: 添加对象到容器 </p>
 *
 * @author sujay
 * @since 09:50 2023/08/03
 * @version 1.0 JDK1.8
 */
public interface InstantiationComponent {

    default void instantiationComponent() {
        DispatchComponentProvider.getInstance().addComponent(this);
    }
}
