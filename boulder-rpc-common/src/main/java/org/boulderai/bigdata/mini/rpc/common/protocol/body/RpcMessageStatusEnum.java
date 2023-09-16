package org.boulderai.bigdata.mini.rpc.common.protocol.body;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.common.protocol.body
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/16 2:19 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public enum RpcMessageStatusEnum {
    SUCCESS(0, "success status"),
    FAIL(1, "failed status");

    RpcMessageStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
