package org.boulderai.bigdata.mini.rpc.common.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.common.scanner
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/27 5:07 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class ClassScanner {

    protected static List<String> getServiceClassNameList(String packageName, boolean recursive) throws Exception {
        List<String> serviceClassNameList = new ArrayList<>();
        String packageDirName = packageName.replace(".", "/");
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, serviceClassNameList);
            } else if ("jar".equals(protocol)) {
                //todo
                findAndAddClassesInPackageByJar(packageName, recursive, serviceClassNameList, packageDirName, url);
            }
        }

        return serviceClassNameList;
    }

    private static String findAndAddClassesInPackageByJar(String packageName, boolean recursive, List<String> serviceClassNameList, String packageDirName, URL url) throws IOException {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            if (name.startsWith(packageDirName)) {
                int index = name.lastIndexOf("/");
                if (index != -1) {
                    packageName = name.substring(0, index).replace("/", ".");
                    if (name.endsWith(".class") && !jarEntry.isDirectory()) {
                        //去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - ".class".length());
                        serviceClassNameList.add(packageName + '.' + className);
                    }
                    //todo
                }
            }
        }

        return packageName;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String filePath, boolean recursive, List<String> serviceClassNameList) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles((file) -> {
            return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
        });
        for (File file : files) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, serviceClassNameList);
            } else {
                String className = file.getName().split("\\.class")[0];
                serviceClassNameList.add(packageName + "." + className);
            }
        }
    }

}
