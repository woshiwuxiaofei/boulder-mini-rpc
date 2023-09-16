package org.boulderai.bigdata.mini.rpc.server;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/5 5:55 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class RpcNativeServerTest {

    RpcNativeServer singleServer;

    @Before
    public void setUp() throws Exception {
        singleServer = new RpcNativeServer(
                "127.0.0.1:27880",
                1,1, 5,
                "lru","org.boulderai.bigdata.mini.rpc");
    }

    @Test
    public void startRpcSingleServer() throws Exception {
        singleServer.start();
    }

    public void tearDown() throws Exception {
    }
}