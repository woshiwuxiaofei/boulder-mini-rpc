package org.boulderai.bigdata.mini.rpc.registry.zookeeper;


import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.boulderai.bigdata.mini.rpc.common.helper.RpcServiceHelper;
import org.boulderai.bigdata.mini.rpc.common.meta.ServiceMetadata;
import org.boulderai.bigdata.mini.rpc.registry.api.ServiceRegistry;
import org.boulderai.bigdata.mini.rpc.registry.api.config.RegistryConfig;

import java.util.List;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.registry.zookeeper
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/17 2:11 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private ServiceDiscovery<ServiceMetadata> serviceDiscovery;

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddress(), new ExponentialBackoffRetry(1000, 3));
        client.start();
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetadata.class)
                .client(client)
                .serializer(new JsonInstanceSerializer<>(ServiceMetadata.class))
                .basePath("/boulder-mini-rpc")
                .build();
        serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMetadata serviceMeta) throws Exception {
        ServiceInstance<ServiceMetadata> serviceInstance = ServiceInstance.<ServiceMetadata>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddress())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(ServiceMetadata serviceMeta) throws Exception {
        ServiceInstance<ServiceMetadata> serviceInstance = ServiceInstance.<ServiceMetadata>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddress())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public List<ServiceMetadata> discoveryAll() throws Exception {
        return null;
    }
}
