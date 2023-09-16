package org.boulderai.bigdata.mini.rpc.server.connection;

import io.netty.channel.Channel;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server.connection
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/14 5:28 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public class ConnectionInfo implements Serializable {
    private static final long serialVersionUID = -3674162284204311065L;

    /**
     * Channel连接
     */
    private Channel channel;

    /**
     * 连接时间
     */
    private long connectionTime;

    /**
     * 最后使用的时间
     */
    private long lastUsedTime;

    /**
     * 最后使用的次数
     */
    private AtomicInteger usedCount = new AtomicInteger(0);

    public ConnectionInfo() {
    }

    public ConnectionInfo(Channel channel) {
        this.channel = channel;
        this.connectionTime = System.currentTimeMillis();
        this.lastUsedTime = System.currentTimeMillis();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(long connectionTime) {
        this.connectionTime = connectionTime;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public AtomicInteger getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(AtomicInteger usedCount) {
        this.usedCount = usedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionInfo that = (ConnectionInfo) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
