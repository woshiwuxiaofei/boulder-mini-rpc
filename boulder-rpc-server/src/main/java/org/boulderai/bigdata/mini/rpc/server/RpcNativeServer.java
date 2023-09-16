package org.boulderai.bigdata.mini.rpc.server;

import org.boulderai.bigdata.mini.rpc.server.scanner.RpcServiceScanner;

import java.util.Map;

public class RpcNativeServer extends BaseNettyServer {

    public RpcNativeServer(String serverAddress, int corePoolSize, int maximumPoolSize, int maxConnections, String unusedStrategyType, String scanPackage) {
        super(serverAddress, corePoolSize, maximumPoolSize, maxConnections, unusedStrategyType);
        this.serviceBeanMap = RpcServiceScanner.scanWithRpcServiceAnnotation(serverAddress, scanPackage, null);
    }
}
