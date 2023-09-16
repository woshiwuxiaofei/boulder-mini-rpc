package org.boulderai.bigdata.mini.rpc.common.helper;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.common.helper
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/27 3:53 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class RpcServiceHelper {

    public static String buildServiceKey(String serviceName, String serviceVersion, String serviceGroup) {
        return String.join("#", serviceName, serviceVersion, serviceGroup);
    }

}
