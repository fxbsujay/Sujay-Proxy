package com.susu.proxy.server.web.servlet;

import com.alibaba.fastjson.JSONObject;
import com.susu.proxy.core.common.utils.ClassUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.server.web.annotation.*;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: Spring Web Mini</p>
 * <p>Description: 缩小版的 Spring Web 实现 </p>
 *
 * @author sujay
 * @since 13:13 2023/07/17
 * @version 1.0 JDK1.8
 */
@Slf4j
public abstract class AbstractDispatcherServlet extends HttpServlet {

    /**
     * BASE_PACKAGE 路径下扫描到的所有类名
     */
    private Set<Class<?>> classes = new HashSet<>();

    /**
     * 路径变量解析器
     */
    private final VariablePathParser variablePathParser = new VariablePathParser();


    /**
     * Tomcat Servlet 扫描路径
     */
    private static final String BASE_PACKAGE = "com.susu.proxy.server.web.servlet";

    /**
     *  key:    类名
     *  value:  反射生成的bean对象名称，和spring的bean是一个概念
     */
    protected final Map<String, String> classNameToBeanNameMap = new ConcurrentHashMap<>();

    /**
     *  key:    bean名称
     *  value:  发射生成的类对象
     */
    protected final Map<String, Object> beanNameToInstanceMap = new ConcurrentHashMap<>();

    /**
     *  key:    uri#method
     *  value:  mapping
     */
    private final Map<String, Mapping> mappings = new ConcurrentHashMap<>();

    public AbstractDispatcherServlet() {
        this(BASE_PACKAGE);
    }

    public AbstractDispatcherServlet(String basePackage) {
        try {
            initController(basePackage);
            injectControllerComponent();
            createRequestMapping();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 加载所有加了 RestController 注解的类
     */
    public void initController(String basePackage) throws InstantiationException, IllegalAccessException {
        this.classes = ClassUtils.scanPackageByAnnotation(basePackage, RestController.class);
        for (Class<?> clazz : this.classes) {
            RestController annotation = clazz.getAnnotation(RestController.class);
            String beanName = annotation.value();
            if (StringUtils.isBlank(beanName)) {
                beanName = clazz.getSimpleName();
            }
            beanNameToInstanceMap.put(beanName, clazz.newInstance());
            classNameToBeanNameMap.put(clazz.getCanonicalName(), beanName);
        }
    }

    /**
     * 给 Controller 类内的加了 Autowired 注解类进行 Bean 注入
     *
     * @throws IllegalAccessException Bean 找不到异常
     */
    private void injectControllerComponent() throws IllegalAccessException {
        for (Map.Entry<String, Object> entry : beanNameToInstanceMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }

                Autowired annotation = field.getAnnotation(Autowired.class);
                String needInjectBeanName = annotation.value();

                if (needInjectBeanName.length() == 0) {
                    needInjectBeanName = field.getType().getSimpleName();
                }
                field.setAccessible(true);
                Object component = DispatchComponentProvider.getInstance().getComponent(needInjectBeanName);
                if (component == null) {
                    throw new IllegalArgumentException("Can not find component " + needInjectBeanName);
                }
                field.set(entry.getValue(), component);

                if (log.isDebugEnabled()) {
                    log.debug("Inject controller components：[{}] ", entry.getValue().getClass().getSimpleName() + "#" + field.getName());
                }
            }
        }
    }


    /**
     *  生成请求路径与对应方法的映射关系
     */
    private void createRequestMapping() {
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(RestController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                baseUrl = annotation.value();
            }

            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                if (!m.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                RequestMapping annotation = m.getAnnotation(RequestMapping.class);
                String path = annotation.value();
                String fullPath = trimUrl(baseUrl + path);

                if (variablePathParser.containVariable(path)) {
                    variablePathParser.add(fullPath);
                    if (log.isDebugEnabled()) {
                        log.debug("Save path variable mapping：[path={}]", fullPath);
                    }
                }

                Mapping mapping = new Mapping();
                String method = annotation.method();
                mapping.setUrl(fullPath);
                mapping.setMethod(method);
                mapping.setInvokeMethod(m);

                parseParameterMetadata(mapping, m.getParameters());

                if (log.isDebugEnabled()) {
                    log.debug("Save URL mapping：[header={}, method={}]", method + " " + fullPath, clazz.getCanonicalName() + "#" + m.getName());
                }

                String key = fullPath + "#" + method;
                if (mappings.containsKey(key)) {
                    throw new IllegalArgumentException("Duplicate interface declaration：" + key);
                }

                mappings.put(key, mapping);
            }
        }
    }


    /**
     * <p>Description: 解析方法所需要的参数</p>
     *
     * @param mapping       请求路径与调用方法映射
     * @param parameters    方法参数数组
     */
    private void parseParameterMetadata(Mapping mapping, Parameter[] parameters) {
        boolean hasRequestBody = false;
        boolean hasQueries = false;

        String name = mapping.getInvokeMethod().getDeclaringClass().getSimpleName() + "#" + mapping.getInvokeMethod().getName();

        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                PathVariable annotation = parameter.getAnnotation(PathVariable.class);
                String variableKey = annotation.value();
                mapping.addParameterList(Mapping.Type.PATH_VARIABLE, variableKey, parameter.getType());
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {

                if (hasRequestBody) {
                    throw new IllegalArgumentException("Only one @RequestBody annotation is supported on the same interface: " + name);
                }

                hasRequestBody = true;
                Class<?> type = parameter.getType();
                mapping.addParameterList(Mapping.Type.REQUEST_BODY, null, type);
            } else {

                if (hasQueries) {
                    throw new IllegalArgumentException("Only one get request entity is supported：" + name);
                }

                hasQueries = true;
                Class<?> type = parameter.getType();
                mapping.addParameterList(Mapping.Type.QUERY_ENTITY, null, type);
            }
        }
    }


    /**
     * <p>Description: 将请求参数封装成方法入参</p>
     *
     * @param mapping 请求映射关系
     * @param request 请求
     * @return 调用参数
     * @throws Exception 异常
     */
    protected Object[] generateParameter(Mapping mapping, HttpServletRequest request) throws Exception {

        if (mapping.getParameterList().isEmpty()) {
            return new Object[0];
        }

        Map<String, String> pathVariableMap = null;
        List<Mapping.ParamMetadata> parameterList = mapping.getParameterList();
        Object[] params = new Object[parameterList.size()];

        for (int i = 0; i < parameterList.size(); i++) {
            Mapping.ParamMetadata metadata = parameterList.get(i);

            if (metadata.getType().equals(Mapping.Type.PATH_VARIABLE)) {

                if (pathVariableMap == null) {
                    pathVariableMap = variablePathParser.extractVariable(trimUrl(request.getRequestURI()));
                }

                String pathVariableValue = pathVariableMap.get(metadata.getParamKey());
                Class<?> classType = metadata.getParamClassType();
                params[i] = mapValue(classType, pathVariableValue);

            } else if (metadata.getType().equals(Mapping.Type.REQUEST_BODY)) {

                if (request.getMethod().equals(HttpMethod.GET.toString())) {
                    throw new IllegalArgumentException("@RequestBody注解不支持GET请求方式");
                }

                if (!request.getContentType().contains("application/json")) {
                    throw new IllegalArgumentException("@RequestBody注解只支持json格式数据");
                }

                String json = readInput(request.getInputStream());
                try {
                    params[i] = JSONObject.parseObject(json, metadata.getParamClassType());
                } catch (Exception e) {
                    throw new IllegalArgumentException("JSON格式有误： " + json);
                }

            } else if (metadata.getType().equals(Mapping.Type.QUERY_ENTITY)) {

                Map<String, String> queriesMap = extractQueries(request);
                Class<?> paramClassType = metadata.getParamClassType();
                Field[] declaredFields = paramClassType.getDeclaredFields();
                Object param = paramClassType.newInstance();

                for (Field field : declaredFields) {
                    String name = field.getName();
                    String value = queriesMap.get(name);
                    if (value == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(param, mapValue(field.getType(), value));
                }
                params[i] = param;
            }
        }
        return params;
    }

    /**
     * <p>Description: 解析url地址</p>
     *
     * <pre>
     *     For example:
     *         //github.com/../
     *         /github.com/..
     * </pre>
     *
     * @param uri   请求路径
     */
    protected String trimUrl(String uri) {
        uri = uri.replaceAll("//", "/");
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }


    /**
     * <p>Description: 将参数转化为具体的类型</p>
     * <p>Description: Convert parameters to specific types</p>
     *
     * @param classType 参数类型
     * @param value     参数值
     * @return 转换后的结果
     */
    private Object mapValue(Class<?> classType, String value) {
        if (classType.equals(String.class)) {
            return value;
        } else if (classType.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (classType.equals(Long.class)) {
            return Long.parseLong(value);
        }
        return value;
    }

    /**
     * <p>Description: 根据url和servlet方法名获取映射</p>
     * <p>Description: Get the mapping according to the URL and servlet method name</p>
     *
     * @param uri       路径
     * @param method    方法名
     * @return          接口映射
     */
    protected Mapping findMapping(String uri, String method) {
        Mapping mapping = mappings.get(uri + "#" + method);
        if (mapping != null) {
            return mapping;
        }
        String urlWithVariable = variablePathParser.match(uri);
        if (urlWithVariable != null) {
            mapping = mappings.get(urlWithVariable + "#" + method);
        }
        return mapping;
    }


    /**
     * 读取请求中的输入流
     */
    private String readInput(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        return sb.toString();
    }

    /**
     * 提取请求中的请求参数
     */
    private Map<String, String> extractQueries(HttpServletRequest request) {
        if (request.getQueryString() == null) {
            return new HashMap<>(2);
        }

        StringTokenizer st = new StringTokenizer(request.getQueryString(), "&");
        int i;
        Map<String, String> queries = new HashMap<>(2);

        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            i = s.indexOf("=");

            if (i > 0 && s.length() >= i + 1) {
                String name = s.substring(0, i);
                String value = s.substring(i + 1);

                try {
                    name = URLDecoder.decode(name, "UTF-8");
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                queries.put(name, value);

            } else if (i == -1) {
                String name = s;
                String value = "";

                try {
                    name = URLDecoder.decode(name, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                queries.put(name, value);
            }
        }
        return queries;
    }
}
