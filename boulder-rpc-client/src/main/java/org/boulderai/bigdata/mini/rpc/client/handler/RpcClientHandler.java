package org.boulderai.bigdata.mini.rpc.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.boulderai.bigdata.mini.rpc.common.protocol.RpcProtocol;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcMessageTypeEnum;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcResponse;
import org.boulderai.bigdata.mini.rpc.common.protocol.header.RpcHeader;
import org.boulderai.bigdata.mini.rpc.common.thread.ThreadPool;
import org.boulderai.bigdata.mini.rpc.proxy.api.future.RpcFuture;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.client.handler
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/16 12:08 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    //存储请求ID与RpcResponse协议的映射关系
    private static final Map<Long, RpcFuture> pendingRPC = new ConcurrentHashMap<>();

    /**
     * 线程池
     */
    private final ThreadPool threadPool;

    public RpcClientHandler(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null) {
            return;
        }
        threadPool.submit(() -> {
            this.handleMessage(protocol, ctx.channel());
        });
    }

    private void handleMessage(RpcProtocol<RpcResponse> protocol, Channel channel) {
        RpcHeader header = protocol.getHeader();
        if (header.getMsgType() == (byte) RpcMessageTypeEnum.RESPONSE.getType()) {
            this.handleResponseMessageOrBuffer(protocol);
        }
    }

    private void handleResponseMessageOrBuffer(RpcProtocol<RpcResponse> protocol) {
        long requestId = protocol.getHeader().getRequestId();
        RpcFuture rpcFuture = pendingRPC.remove(requestId);
        if (Objects.nonNull(rpcFuture)) {
            rpcFuture.done(protocol);
        }
    }
}
