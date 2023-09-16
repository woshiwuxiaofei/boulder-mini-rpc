package org.boulderai.bigdata.mini.rpc.common.meta;

import java.io.Serializable;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.common.meta
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/9/16 3:28 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class ServiceMetadata implements Serializable {
    private static final long serialVersionUID = -2019862994688365890L;

    public ServiceMetadata() {
    }

    public ServiceMetadata(String serviceAddress, String serviceName, String serviceVersion, String serviceGroup, int weight) {
        this.serviceAddress = serviceAddress;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.weight = weight;
    }

    /**
     * 服务地址
     */
    private String serviceAddress;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 服务分组
     */
    private String serviceGroup;

    /**
     * 服务提供者实例的权重
     */
    private int weight;

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }



}
