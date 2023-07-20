package com.susu.proxy.server.web.eum;


import com.susu.proxy.core.common.eum.PacketType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentTypeEnum {

    JPG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    ICO("ico","image/x-icon"),
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript");

    /**
     * code 文件后缀
     */
    private final String suffix;

    /**
     * message 文件编码
     */
    private final String content;

    public static String getContent(String suffix) {
        for (ContentTypeEnum contentType : values()) {
            if (contentType.getSuffix().equals(suffix)) {
                return contentType.getContent();
            }
        }
        return null;
    }

}
