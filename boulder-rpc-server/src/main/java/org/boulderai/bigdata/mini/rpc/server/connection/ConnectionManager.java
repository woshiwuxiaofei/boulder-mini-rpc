package org.boulderai.bigdata.mini.rpc.server.connection;

import com.boulderai.bigdata.mini.rpc.spi.loader.ExtensionLoader;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.boulderai.bigdata.mini.rpc.common.constants.RpcConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server.connection
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/14 5:27 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class ConnectionManager {

    private volatile Map<String, ConnectionInfo> connectionMap = new ConcurrentHashMap<>();

    private final int maxConnections;

    private final UnusedStrategy unusedStrategy;

    private static volatile ConnectionManager instance;

    public ConnectionManager(int maxConnections, String unusedStrategyType) {
        this.maxConnections = maxConnections <= 0 ? Integer.MAX_VALUE : maxConnections;
        unusedStrategyType = StringUtils.isEmpty(unusedStrategyType)
                ? RpcConstants.RPC_CONNECTION_UNUSED_STRATEGY_DEFAULT
                : unusedStrategyType;
        this.unusedStrategy = ExtensionLoader.getExtension(UnusedStrategy.class, unusedStrategyType);
    }

    public static ConnectionManager getInstance(int maxConnections, String unusedStrategyType) {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager(maxConnections, unusedStrategyType);
                }
            }
        }
        return instance;
    }

    public void add(Channel channel) {
        ConnectionInfo connectionInfo = new ConnectionInfo(channel);
        if (this.checkConnectionList(connectionInfo)) {
            connectionMap.put(getKey(channel), connectionInfo);
        }
    }

    public void remove(Channel channel) {
        connectionMap.remove(getKey(channel));
    }

    public void update(Channel channel) {
        ConnectionInfo connectionInfo = connectionMap.get(getKey(channel));
        connectionInfo.setLastUsedTime(System.currentTimeMillis());
        connectionInfo.getUsedCount().incrementAndGet();
        connectionMap.put(getKey(channel), connectionInfo);
    }

    private boolean checkConnectionList(ConnectionInfo connectionInfo) {
        List<ConnectionInfo> allConnectionList = new ArrayList<>(connectionMap.values());
        if (allConnectionList.size() >= maxConnections) {
            try {
                ConnectionInfo selectedConnection = unusedStrategy.selectConnection(allConnectionList);
                if (Objects.nonNull(selectedConnection)) {
                    selectedConnection.getChannel().close();
                    connectionMap.remove(getKey(selectedConnection.getChannel()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                connectionInfo.getChannel().close();
                return false;
            }
        }
        return true;
    }

    private String getKey(Channel channel) {
        return channel.id().asLongText();
    }

}
