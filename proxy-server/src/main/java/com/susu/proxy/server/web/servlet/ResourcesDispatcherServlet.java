package com.susu.proxy.server.web.servlet;

import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.server.web.eum.ContentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Description: 处理静态资源文件的请求  xxx.png，xxx.html...</p>
 *
 * @author fxbsujay@gmail.com
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ResourcesDispatcherServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String filePath = req.getRequestURI();
        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() -1);
        }

        String extension = getExtension(filePath);

        if (StringUtils.isBlank(filePath) || StringUtils.isBlank(extension)) {
            filePath = "/index.html";
            extension = "html";
        }

        String content = ContentTypeEnum.getContent(extension);
        if (content == null) {
            log.error("Unsupported file formats : {}", extension);
            error(resp);
            return;
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", content + ";charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(content + ";charset=UTF-8");
        ServletOutputStream outputStream = resp.getOutputStream();
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("static" + filePath);
        if (resource == null) {
            log.error("No resources found : {}", filePath);
            error(resp);
            return;
        }

        int len = 0;
        byte[] buffer = new byte[1024];
        while((len = resource.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
    }

    /**
     * <p>Description: response result</p>
     *
     * @param result  返回结果
     * @throws IOException  IO异常
     */
    private void error(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * <p>Description: Get file extension</p>
     * <p>获取文件名后缀</p>
     *
     * @param filename 文件名
     */
    public static String getExtension(String filename) {
        String[] split = filename.split("\\.");
        if (split.length > 1) {
            return split[split.length -1];
        }
        return null;
    }
}
