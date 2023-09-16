package org.boulderai.bigdata.mini.rpc.server.connection;

import com.boulderai.bigdata.mini.rpc.spi.annotation.SPI;
import org.boulderai.bigdata.mini.rpc.common.constants.RpcConstants;

import java.util.List;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server.connection
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/14 6:28 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
@SPI(RpcConstants.RPC_CONNECTION_UNUSED_STRATEGY_DEFAULT)
public interface UnusedStrategy {
    ConnectionInfo selectConnection(List<ConnectionInfo> connectionList);
}
