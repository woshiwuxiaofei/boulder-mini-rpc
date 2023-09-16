package org.boulderai.bigdata.mini.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boulderai.bigdata.mini.rpc.client.handler.RpcClientHandler;
import org.boulderai.bigdata.mini.rpc.client.handler.RpcClientHandlerCacher;
import org.boulderai.bigdata.mini.rpc.common.codec.RpcDecoder;
import org.boulderai.bigdata.mini.rpc.common.codec.RpcEncoder;
import org.boulderai.bigdata.mini.rpc.common.constants.RpcConstants;
import org.boulderai.bigdata.mini.rpc.common.meta.ServiceMetadata;
import org.boulderai.bigdata.mini.rpc.common.thread.ThreadPool;
import org.boulderai.bigdata.mini.rpc.registry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.client
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/13 10:16 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
@Slf4j
public class BaseNettyClient {

    private static volatile BaseNettyClient nettyClientInstance;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    //心跳间隔时间，默认30秒
    private int heartbeatInterval = 30000;
    //并发处理线程池
    private ThreadPool threadPool;

    //是否开启直连服务
    private boolean enableDirectServer = false;
    //直连服务的地址
    private String directServerUrl;

    private BaseNettyClient() {
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup();

    }

    public BaseNettyClient getInstance() {
        if (nettyClientInstance == null) {
            synchronized (BaseNettyClient.class) {
                if (nettyClientInstance == null) {
                    nettyClientInstance = new BaseNettyClient();
                }
            }
        }
        return nettyClientInstance;
    }

    public BaseNettyClient buildInitializer() {
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder())//outbound
                                .addLast(RpcConstants.CODEC_DECODER, new RpcDecoder())//Inbound
                                .addLast(RpcConstants.CODEC_CLIENT_IDLE_HANDLER, new IdleStateHandler(heartbeatInterval,0, 0, TimeUnit.MILLISECONDS))//Inbound
                                .addLast(RpcConstants.CODEC_HANDLER, new RpcClientHandler(threadPool));
                    }
                });

        return this;
    }

    public BaseNettyClient buildConnection(ServiceRegistry serviceRegistry) {
        this.initConnection(serviceRegistry);

        return this;
    }

    private void initConnection(ServiceRegistry serviceRegistry) {
        List<ServiceMetadata> serviceMetaList = new ArrayList<>();
        if (enableDirectServer && Objects.nonNull(directServerUrl)) {
            String[] allServerUrls = directServerUrl.split(RpcConstants.COMMA);
            for (String eachServerUrl : allServerUrls) {
                if (StringUtils.isBlank(eachServerUrl)) {
                    continue;
                }
                ServiceMetadata serviceMetadata = new ServiceMetadata();
                serviceMetadata.setServiceAddress(eachServerUrl);
                serviceMetaList.add(serviceMetadata);
            }
        } else {
            try {
                serviceMetaList.addAll(serviceRegistry.discoveryAll());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (ServiceMetadata serviceMetadata : serviceMetaList) {
            RpcClientHandler rpcClientHandler = null;
            try {
                rpcClientHandler = this.getRpcClientHandler(serviceMetadata);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            RpcClientHandlerCacher.put(serviceMetadata, rpcClientHandler);
        }
    }

    private RpcClientHandler getRpcClientHandler(ServiceMetadata serviceMetadata) throws InterruptedException {
        String[] ipPort = serviceMetadata.getServiceAddress().split(RpcConstants.IP_PORT_SPLIT);
        ChannelFuture channelFuture = bootstrap.connect(ipPort[0], Integer.valueOf(ipPort[1])).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {}  success.", serviceMetadata.getServiceAddress());
            } else {
                log.error("connect rpc server {}  failed.", serviceMetadata.getServiceAddress());
            }
        });
        return channelFuture.channel().pipeline().get(RpcClientHandler.class);
    }




}
