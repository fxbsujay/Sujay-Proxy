package com.susu.proxy.core.config;

import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.stereotype.Configuration;
import lombok.Data;

@Data
@Configuration("app")
public class AppConfig {

    public static String name = StringUtils.uuid();
}
