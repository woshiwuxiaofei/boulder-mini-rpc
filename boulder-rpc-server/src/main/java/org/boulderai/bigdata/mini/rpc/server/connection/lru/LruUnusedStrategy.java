package org.boulderai.bigdata.mini.rpc.server.connection.lru;

import com.boulderai.bigdata.mini.rpc.spi.annotation.SPIClass;
import org.apache.commons.collections4.CollectionUtils;
import org.boulderai.bigdata.mini.rpc.server.connection.ConnectionInfo;
import org.boulderai.bigdata.mini.rpc.server.connection.UnusedStrategy;

import java.util.Collections;
import java.util.List;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server.connection.lru
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/14 6:38 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
@SPIClass
public class LruUnusedStrategy implements UnusedStrategy {
    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        if (CollectionUtils.isEmpty(connectionList)) {
            return null;
        }
        connectionList.sort((c1, c2) -> c1.getLastUsedTime() - c2.getLastUsedTime() > 0 ? 1 : -1);
        return connectionList.get(0);
    }
}
