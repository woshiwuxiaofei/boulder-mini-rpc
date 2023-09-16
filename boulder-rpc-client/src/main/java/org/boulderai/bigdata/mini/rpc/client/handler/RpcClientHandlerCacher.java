package org.boulderai.bigdata.mini.rpc.client.handler;

import org.boulderai.bigdata.mini.rpc.common.meta.ServiceMetadata;

import java.util.concurrent.ConcurrentHashMap;

import static org.boulderai.bigdata.mini.rpc.common.constants.RpcConstants.UNDERLINE;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.client.handler
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/16 3:19 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class RpcClientHandlerCacher {

    private static ConcurrentHashMap<String, RpcClientHandler> rpcClientHandlerCache;

    static {
        rpcClientHandlerCache = new ConcurrentHashMap<>();
    }

    public static RpcClientHandler get(ServiceMetadata metadata) {
        return rpcClientHandlerCache.get(getKey(metadata));
    }

    public static void put(ServiceMetadata metadata, RpcClientHandler rpcClientHandler) {
        rpcClientHandlerCache.put(getKey(metadata), rpcClientHandler);
    }

    private static String getKey(ServiceMetadata metadata) {
        return metadata.getServiceAddress();
    }
}
