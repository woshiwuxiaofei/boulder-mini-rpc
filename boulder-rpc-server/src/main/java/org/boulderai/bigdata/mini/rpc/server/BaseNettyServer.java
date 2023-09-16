package org.boulderai.bigdata.mini.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.boulderai.bigdata.mini.rpc.common.codec.RpcDecoder;
import org.boulderai.bigdata.mini.rpc.common.codec.RpcEncoder;
import org.boulderai.bigdata.mini.rpc.common.constants.RpcConstants;
import org.boulderai.bigdata.mini.rpc.server.handler.RpcServerHandler;

@Slf4j
public class BaseNettyServer implements IServer {

    /**
     * 主机ip：port
     */
    private String host = "127.0.0.1";
    private int port = 88888;
    /**
     *  服务key:bean
     */
    protected Map<String, Object> serviceBeanMap = new HashMap<>();
    /**
     * 异步处理请求的线程池参数
     */
    private int corePoolSize;
    private int maximumPoolSize;
    /**
     * 连接相关参数
     */
    private int maxConnections;
    private String unusedStrategyType;
    private int heartbeatInterval = 30000;

    public BaseNettyServer(String serverAddress, int corePoolSize, int maximumPoolSize, int maxConnections, String unusedStrategyType) {
        if (!StringUtils.isEmpty(serverAddress)){
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.maxConnections = maxConnections;
        this.unusedStrategyType = unusedStrategyType;
    }

    @Override
    public void start() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(RpcConstants.CODEC_DECODER, new RpcDecoder())//Inbound
                            .addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder())//outbound
                            .addLast(RpcConstants.CODEC_SERVER_IDLE_HANDLER, new IdleStateHandler(0, 0, heartbeatInterval, TimeUnit.MILLISECONDS))//Inbound
                            .addLast(RpcConstants.CODEC_HANDLER, new RpcServerHandler(serviceBeanMap, corePoolSize, maximumPoolSize, maxConnections, unusedStrategyType));
                    }
                })
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            log.info("Server started on {}:{}", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("RPC Server start error", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    @Override
    public void stop() {
        //todo
    }
}
