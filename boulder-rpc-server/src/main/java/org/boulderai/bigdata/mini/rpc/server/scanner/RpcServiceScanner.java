package org.boulderai.bigdata.mini.rpc.server.scanner;

import org.apache.commons.collections4.CollectionUtils;
import org.boulderai.bigdata.mini.rpc.common.helper.RpcServiceHelper;
import org.boulderai.bigdata.mini.rpc.common.meta.ServiceMetadata;
import org.boulderai.bigdata.mini.rpc.common.scanner.ClassScanner;
import org.boulderai.bigdata.mini.rpc.common.scanner.annotation.RpcService;
import org.boulderai.bigdata.mini.rpc.registry.ServiceRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.common.scanner
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/27 5:17 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class RpcServiceScanner extends ClassScanner {
    public static Map<String, Object> scanWithRpcServiceAnnotation(String address, String packageName, ServiceRegistry serviceRegistry) {
        Map<String, Object> serviceBeanMap = new HashMap<>();
        List<String> serviceClassNameList = getServiceClassNameList(packageName, true);
        if (CollectionUtils.isEmpty(serviceClassNameList)) {
            return serviceBeanMap;
        }
        for (String className : serviceClassNameList) {
            try {
                Class<?> serviceClazz = Class.forName(className);
                RpcService rpcService = serviceClazz.getAnnotation(RpcService.class);
                if (Objects.nonNull(rpcService)) {
                    ServiceMetadata serviceMeta = new ServiceMetadata(address, rpcService.interfaceClassName(), rpcService.version(), rpcService.group(), rpcService.weight());
                    //serviceRegistry.register(serviceMeta);
                    serviceBeanMap.put(RpcServiceHelper.buildServiceKey(rpcService.interfaceClassName(), rpcService.version(), rpcService.group()), serviceClazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serviceBeanMap;
    }
}
