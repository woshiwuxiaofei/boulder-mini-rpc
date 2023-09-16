package com.boulderai.bigdata.mini.rpc.spi.loader;

import com.boulderai.bigdata.mini.rpc.spi.annotation.SPI;
import com.boulderai.bigdata.mini.rpc.spi.annotation.SPIClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package com.boulderai.bigdata.mini.rpc.spi.loader
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/14 7:01 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/

public final class ExtensionLoader<T> {

    private static final String SERVICES_DIRECTORY  = "META-INF/services/";
    private static final String SERVICES_DIRECTORY2 = "META-INF/internal/";

    private static final String[] SPI_DIRECTORIES = new String[] {
            SERVICES_DIRECTORY,
            SERVICES_DIRECTORY2
    };

    private static final Map<Class<?>, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<T> clazz;

    private final ClassLoader classLoader;

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final Map<String, Holder<T>> cachedInstances = new ConcurrentHashMap<>();

    private String cachedDefaultName;

    private ExtensionLoader(final Class<T> clazz, final ClassLoader classLoader) {
        this.clazz = clazz;
        this.classLoader = classLoader;
    }

    public static <T> T getExtension(final Class<T> clazz, String name) {
        return  StringUtils.isEmpty(name)
                ? getExtensionLoader(clazz).getDefaultSpiClassInstance()
                : getExtensionLoader(clazz).getSpiClassInstance(name);
    }

    public static <T>  ExtensionLoader<T> getExtensionLoader(final Class<T> clazz) {
        return getExtensionLoader(clazz, ExtensionLoader.class.getClassLoader());
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz, final ClassLoader classLoader) {
        Objects.requireNonNull(clazz, "extension clazz is null!");

        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") is not interface!");
        }
        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") is not over by SPI annotation!");
        }

        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) LOADERS.get(clazz);
        if (Objects.nonNull(extensionLoader)) {
            return extensionLoader;
        }
        LOADERS.put(clazz, new ExtensionLoader<>(clazz, classLoader));
        return (ExtensionLoader<T>) LOADERS.get(clazz);
    }

    private T getSpiClassInstance(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new NullPointerException("name is null when getting spi class!");
        }
        Holder<T> holder = cachedInstances.get(name);
        if (Objects.isNull(holder)) {
            cachedInstances.put(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        T value = holder.getValue();
        if (Objects.isNull(value)) {
            synchronized (cachedInstances) {
                value = holder.getValue();
                if (Objects.isNull(value)) {
                    value = createExtension(name);
                    holder.setValue(value);
                }

            }
        }
        return value;
    }

    private T getDefaultSpiClassInstance() {
        return getSpiClassInstance(cachedDefaultName);
    }

    private T createExtension(final String name) {
        Class<?> cachedSubClass = getExtensionClasses().get(name);
        if (Objects.isNull(cachedSubClass)) {
            throw new IllegalArgumentException("name is error");
        }
        try {
            return (T) cachedSubClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: "
                    + cachedSubClass + ")  could not be instantiated: " + e.getMessage(), e);
        }
    }

    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.getValue();
        if (Objects.isNull(classes)) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (Objects.isNull(classes)) {
                    classes = loadExtensionClass();
                    cachedClasses.setValue(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClass() {
        SPI annotation = clazz.getAnnotation(SPI.class);
        if (Objects.nonNull(annotation)) {
            String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                cachedDefaultName = value;
            }
        }
        //spi value : spi implement class
        Map<String, Class<?>> classes = new HashMap<>();
        loadDirectory(classes);
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> classes) {
        for (String directory : SPI_DIRECTORIES) {
            String fileName = directory + clazz.getName();
            try {
                Enumeration<URL> urls = Objects.nonNull(classLoader)
                        ? classLoader.getResources(fileName)
                        : ClassLoader.getSystemResources(fileName);
                if (Objects.nonNull(urls)) {
                    while (urls.hasMoreElements()) {
                        URL url = urls.nextElement();
                        loadResources(classes, url);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadResources(Map<String, Class<?>> classes, URL url) {
        try (InputStream inputStream = url.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.forEach((k, v) -> {
                String name = (String) k;
                String classpath = (String) v;
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classpath)) {
                    try {
                        loadClass(classes, name, classpath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClass(Map<String, Class<?>> classes, String name, String classpath) throws Exception {
        Class<?> subClass = Objects.nonNull(this.classLoader)
                ? Class.forName(classpath, true, this.classLoader)
                : Class.forName(classpath);
        if (!clazz.isAssignableFrom(subClass)) {
            throw new IllegalStateException("load extension resources error," + subClass + " subtype is not of " + clazz);
        }
        if (!subClass.isAnnotationPresent(SPIClass.class)) {
            throw new IllegalStateException("load extension resources error," + subClass + " without @" + SPIClass.class + " annotation");
        }
        Class<?> oldClass = classes.get(name);
        if (Objects.isNull(oldClass)) {
            classes.put(name, subClass);
        } else if (!Objects.equals(oldClass, subClass)){
            throw new IllegalStateException("load extension resources error,Duplicate class " + clazz.getName() + " name " + name + " on " + oldClass.getName() + " or " + subClass.getName());
        }

    }


    /**
     * The type Holder.
     *
     * @param <T> the type parameter.
     */
    public static class Holder<T> {

        private volatile T value;

        /**
         * Gets value.
         *
         * @return the value
         */
        public T getValue() {
            return value;
        }

        /**
         * Sets value.
         *
         * @param value the value
         */
        public void setValue(final T value) {
            this.value = value;
        }
    }


}
