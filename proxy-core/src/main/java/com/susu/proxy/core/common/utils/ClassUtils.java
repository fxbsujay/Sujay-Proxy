package com.susu.proxy.core.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Description: Class Utils</p>
 * <p>反射、class操作类</p>
 * @author sujay
 * @version 23:48 2023/07/1
 *
 * @since JDK1.8
 */
public class ClassUtils {

    /**
     * Maps primitive {@code Class}es to their corresponding wrapper {@code Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }


    /**
     * Maps wrapper {@code Class}es to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();

    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }


    public static ClassLoader getContextClassLoader() {
        return System.getSecurityManager() == null ?
                Thread.currentThread().getContextClassLoader() :
                AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
    }

    public static ClassLoader getSystemClassLoader() {
        return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : AccessController.doPrivileged((PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.class.getClassLoader();
            if (null == classLoader) {
                classLoader = getSystemClassLoader();
            }
        }
        return classLoader;
    }

    /**
     * <p>Description: Search all current classes</p>
     * <p>搜索当下所有的类</p>
     */
    public static Set<Class<?>> scanPackage() {
        return scanPackage("");
    }

    /**
     * 搜索当下所有的类
     */
    public static Set<Class<?>> scanPackage(String packageName) {
        return scanPackage(packageName, null);
    }

    /**
     * <p>Description: Search classes</p>
     * <p>搜索包下所有继承该注解的类</p>
     *
     * @param packageName 需要扫描的包路径
     * @param annotationClass 注解类
     */
    public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return scanPackage(packageName, (clazz) -> clazz.isAnnotationPresent(annotationClass));
    }

    /**
     * <p>Description: Search classes</p>
     * <p>搜索包下所有继承该父类的类</p>
     *
     * @param packageName 需要扫描的包路径
     * @param superClass 父类
     */
    public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
        return scanPackage(packageName, (clazz) -> superClass.isAssignableFrom(clazz) && !superClass.equals(clazz));
    }

    public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
        return (new ClassScan(packageName, classFilter)).scan();
    }

    @FunctionalInterface
    public interface Filter<T> {
        boolean accept(T var1);
    }

    public static class ClassScan {

        private final String packageName;
        private final String packageNameWithDot;
        private final String packagePath;
        private final String packageDirName;
        private final Charset charset;
        private ClassLoader classLoader;
        private final Set<Class<?>> classes;

        private final Filter<Class<?>> classFilter;

        /**
         * <p>Description: ClassScan</p>
         *
         * @param packageName 需要扫描的包路径
         * @param classFilter 类过滤器
         */
        public ClassScan(String packageName, Filter<Class<?>> classFilter) {
            if (packageName == null) {
                packageName = "";
            }
            this.packageName = packageName;
            this.packageDirName = packageName.replace('.', File.separatorChar);
            this.packageNameWithDot = packageName.length() > 0 && !this.packageName.endsWith(".") ? packageName.concat(".") : packageName;
            this.packagePath = packageName.replace('.', '/');
            this.charset = StandardCharsets.UTF_8;
            this.classes = new HashSet<>();
            this.classFilter = classFilter;
        }


        public Set<Class<?>> scan() {
            Enumeration<URL> resources;
            try {
                resources = getClassLoader().getResources(this.packagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                switch (url.getProtocol()) {
                    case "file":
                        try {
                            this.scanFile(new File(URLDecoder.decode(url.getFile(), this.charset.toString())), null);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "jar":
                        JarFile jarFile;
                        try {
                            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                            jarFile = jarURLConnection.getJarFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        scanJar(jarFile);
                }
            }

            return classes;
        }

        private void scanFile(File file, String rootDir) {
            if (file.isFile()) {
                String fileName = file.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(rootDir.length(), fileName.length() - 6).replace(File.separatorChar, '.');
                    this.addIfAccept(className);

                } else if (fileName.endsWith(".jar")) {
                    try {
                        this.scanJar(new JarFile(file));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (null != files) {
                    for (File subFile : files) {
                        this.scanFile(subFile, null == rootDir ?  this.subPathBeforePackage(file) : rootDir);
                    }
                }
            }
        }

        private void scanJar(JarFile jar) {
            Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                if (name.startsWith(this.packagePath) && name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    this.addIfAccept(this.loadClass(className));
                }
            }
        }

        private void addIfAccept(String className) {
            if (className != null && className.length() != 0) {
                int classLen = className.length();
                int packageLen = this.packageName.length();
                if (classLen == packageLen) {
                    if (className.equals(this.packageName)) {
                        this.addIfAccept(this.loadClass(className));
                    }
                } else if (classLen > packageLen && (packageNameWithDot.length() == 0 || className.startsWith(this.packageNameWithDot))) {
                    this.addIfAccept(this.loadClass(className));
                }

            }
        }

        private void addIfAccept(Class<?> clazz) {
            if (null != clazz) {
                Filter<Class<?>> classFilter = this.classFilter;
                if (classFilter == null || classFilter.accept(clazz)) {
                    this.classes.add(clazz);
                }
            }
        }

        private Class<?> loadClass(String className) {

            ClassLoader loader = this.classLoader;
            if (null == loader) {
                loader = getClassLoader();
                this.classLoader = loader;
            }

            Class<?> clazz;
            try {
                clazz = Class.forName(className, false, loader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            return clazz;
        }

        private String subPathBeforePackage(File file) {
            String filePath = file.getAbsolutePath();
            if (this.packageDirName.length() != 0 && filePath.length() != 0) {
                int pos = filePath.lastIndexOf(packageDirName);
                if (-1 != pos) {
                    filePath = 0 == pos ? "" : filePath.substring(0, pos);
                }
            }

            if (!filePath.endsWith(File.separator)) {
                filePath = filePath.concat(File.separator);
            }
            return filePath;
        }
    }

    public static void main(String[] args) {
        Set<Class<?>> scan = ClassUtils.scanPackage("com.susu.utils");
        scan.forEach( item -> System.out.println(item.toString()));

        Float f = 0.32F;
        String s = "0.32F";
        System.out.println(ClassUtils.isPrimitiveOrWrapper(f.getClass()));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(s.getClass()));
        System.out.println(ClassUtils.isPrimitiveWrapper(f.getClass()));
        System.out.println(ClassUtils.isPrimitiveWrapper(s.getClass()));
    }
}
