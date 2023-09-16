package org.boulderai.bigdata.mini.rpc.proxy.api.callback;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.proxy.api.callback
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/16 2:43 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public interface AsyncCallback {
    /**
     * 成功后的回调方法
     */
    void onSuccess(Object result);

    /**
     * 异常的回调方法
     */
    void onException(Exception e);
}
