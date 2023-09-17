package org.boulderai.bigdata.mini.rpc.registry.api;

import com.boulderai.bigdata.mini.rpc.spi.annotation.SPI;
import org.boulderai.bigdata.mini.rpc.common.meta.ServiceMetadata;
import org.boulderai.bigdata.mini.rpc.registry.api.config.RegistryConfig;

import java.util.List;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.registry
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/27 5:31 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
@SPI
public interface ServiceRegistry {

    /**
     * 默认初始化方法
     */
    default void init(RegistryConfig registryConfig) throws Exception {

    }

    /** 服务注册
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void register(ServiceMetadata serviceMeta) throws Exception;

    /**
     * 服务取消注册
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void unRegister(ServiceMetadata serviceMeta) throws Exception;


    /**
     * 获取所有的数据
     */
    List<ServiceMetadata> discoveryAll() throws Exception;
}
