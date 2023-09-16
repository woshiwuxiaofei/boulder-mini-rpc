package org.boulderai.bigdata.mini.rpc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.boulderai.bigdata.mini.rpc.common.helper.RpcServiceHelper;
import org.boulderai.bigdata.mini.rpc.common.protocol.RpcProtocol;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcMessageTypeEnum;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcRequest;
import org.boulderai.bigdata.mini.rpc.common.protocol.body.RpcResponse;
import org.boulderai.bigdata.mini.rpc.common.protocol.header.RpcHeader;
import org.boulderai.bigdata.mini.rpc.common.thread.ThreadPool;
import org.boulderai.bigdata.mini.rpc.server.connection.ConnectionManager;
import org.boulderai.bigdata.mini.rpc.server.reflect.ReflectInvoker;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    /**
     * 存储服务提供者中被@RpcService注解标注的类的对象
     * key为：serviceName#serviceVersion#group
     * value为：@RpcService注解标注的类的对象
     */
    private final Map<String, Object> rpcServiceBeanMap;

    /**
     * 线程池
     */
    private final ThreadPool threadPool;

    /**
     * 连接管理器
     */
    private ConnectionManager connectionManager;

    /**
     * 反射调用真实方法的SPI接口
     */
    private ReflectInvoker reflectInvoker;



    public RpcServerHandler(Map<String, Object> rpcServiceBeanMap, int corePoolSize, int maximumPoolSize, int maxConnections, String unUsedStrategyType) {
        this.rpcServiceBeanMap = rpcServiceBeanMap;
        this.threadPool = ThreadPool.getInstance(corePoolSize, maximumPoolSize);
        this.connectionManager = ConnectionManager.getInstance(maxConnections, unUsedStrategyType);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //ServerChannelCache.add(ctx.channel());
        connectionManager.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //ServerChannelCache.remove(ctx.channel());
        connectionManager.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        threadPool.submit(() -> {
            connectionManager.update(channelHandlerContext.channel());
            handleMessage(rpcProtocol);
        });
    }

    private RpcProtocol<RpcResponse> handleMessage(RpcProtocol<RpcRequest> rpcProtocol) {
        RpcHeader header = rpcProtocol.getHeader();
        if (header.getMsgType() == RpcMessageTypeEnum.REQUEST.getType()) {
            return handleRequestMessage(rpcProtocol);
        } else if (header.getMsgType() == RpcMessageTypeEnum.HEARTBEAT_TO_PROVIDER.getType()) {
            return handleHeartbeatMessage(rpcProtocol);
        } else {
            log.error("unsupported type:{}", header.getMsgType());
            return null;
        }
    }

    private RpcProtocol<RpcResponse> handleHeartbeatMessage(RpcProtocol<RpcRequest> rpcProtocol) {
        //todo
        return null;
    }

    private RpcProtocol<RpcResponse> handleRequestMessage(RpcProtocol<RpcRequest> rpcProtocol) {
        RpcHeader header = rpcProtocol.getHeader();
        RpcRequest body = rpcProtocol.getBody();
        log.info("receive request message: {}", header.getRequestId());
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();
        try {
            response.setResult(handle(body));
            response.setAsync(body.isAsync());
            response.setOneway(body.isOneway());
        } catch (Throwable e) {
            e.printStackTrace();
            response.setError(e.getMessage());
        }
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);
        return responseRpcProtocol;
    }

    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceObj = rpcServiceBeanMap.get(serviceKey);
        if (Objects.isNull(serviceObj)) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }
        return this.reflectInvoker.invokeMethod(serviceObj, request.getMethodName(), request.getParameterTypes(), request.getParameters());
    }


}
