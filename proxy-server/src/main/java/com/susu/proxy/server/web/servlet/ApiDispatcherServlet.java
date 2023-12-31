package com.susu.proxy.server.web.servlet;

import com.alibaba.fastjson.JSONObject;
import com.susu.proxy.server.web.entity.Mapping;
import com.susu.proxy.server.web.entity.Result;
import com.susu.proxy.server.web.eum.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * <p>Description: 处理 Controller 的请求</p>
 *
 * @author fxbsujay@gmail.com
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Slf4j
public class ApiDispatcherServlet extends AbstractDispatcherServlet {


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

        String uri = trimUrl(req.getRequestURI());
        String method = req.getMethod();
        Mapping mapping = findMapping(uri, method);

        if (mapping == null) {
            responsive(resp, Result.error(ResponseStatusEnum.ERROR_404));
            return;
        }

        if (!"/api/auth/login".equals(uri) && !JwtUtils.check(req)) {
            responsive(resp, Result.error(ResponseStatusEnum.ERROR_401));
            return;
        }
        Method invokeMethod = mapping.getInvokeMethod();
        String className = invokeMethod.getDeclaringClass().getCanonicalName();

        String beanName = classNameToBeanNameMap.get(className);
        Object bean = beanNameToInstanceMap.get(beanName);

        Object result;
        try {
            Object[] args = generateParameter(mapping, req);
            result = invokeMethod.invoke(bean, args);
            responsive(resp, result);
        } catch (Exception e) {

            if (e.getCause() instanceof SysException) {
                SysException cause = (SysException) e.getCause();
                responsive(resp,Result.error(cause.getCode(), cause.getMsg()));
                log.error("Api request failed: [url: {}, message: {}]", uri, cause.getMsg());
            } else {
                responsive(resp, Result.error(ResponseStatusEnum.ERROR_500));
            }
        }
    }

    /**
     * <p>Description: response result</p>
     *
     * @param result  返回结果
     * @throws IOException  IO异常
     */
    private void responsive(HttpServletResponse resp, Object result) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JSONObject.toJSONString(result));
        resp.getWriter().flush();
    }
}
