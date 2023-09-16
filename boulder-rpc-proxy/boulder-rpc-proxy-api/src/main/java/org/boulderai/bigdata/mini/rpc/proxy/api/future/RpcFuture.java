package org.boulderai.bigdata.mini.rpc.proxy.api.future;

import org.boulderai.bigdata.mini.rpc.common.protocol.RpcProtocol;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcMessageStatusEnum;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcRequest;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcResponse;
import org.boulderai.bigdata.mini.rpc.common.thread.ThreadPool;
import org.boulderai.bigdata.mini.rpc.proxy.api.callback.AsyncCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.proxy.api.future
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/16 1:53 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class RpcFuture extends CompletableFuture<Object> {


    private RpcProtocol<RpcRequest>  requestRpcProtocol;
    private RpcProtocol<RpcResponse> responseRpcProtocol;
    private List<AsyncCallback> pendingCallbacks = new ArrayList<>();
    private ThreadPool threadPool;


    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        for (final AsyncCallback pendingCallback : pendingCallbacks) {
            runCallback(pendingCallback);
        }
    }

    private void runCallback(AsyncCallback pendingCallback) {
        RpcResponse response = this.responseRpcProtocol.getBody();
        threadPool.submit(() -> {
            if (response.isError()) {
                pendingCallback.onException(new RuntimeException("response is error:", new Throwable(response.getError())));
            } else {
                pendingCallback.onSuccess(response.getResult());
            }
        });
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        //todo
        return this.getResult(responseRpcProtocol);
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        //todo
        return this.getResult(responseRpcProtocol);
    }

    private Object getResult(RpcProtocol<RpcResponse> responseRpcProtocol){
        if (Objects.isNull(responseRpcProtocol)) {
            return null;
        }
        if (responseRpcProtocol.getHeader().getStatus() == (byte)RpcMessageStatusEnum.FAIL.getCode()) {
            throw new RuntimeException("rpc result cannot get...");
        }

        return responseRpcProtocol.getBody().getResult();
    }

}
