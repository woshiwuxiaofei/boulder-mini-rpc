package org.boulderai.bigdata.mini.rpc.common.protocol.body;

public enum RpcMessageTypeEnum {
    //请求消息
    REQUEST(1),
    //响应消息
    RESPONSE(2),
    //从服务消费者发起的心跳数据
    HEARTBEAT_FROM_CONSUMER(3),
    //服务提供者响应服务消费者的心跳数据
    HEARTBEAT_TO_CONSUMER(4),
    //从服务提供者发起的心跳数据
    HEARTBEAT_FROM_PROVIDER(5),
    //服务消费者响应服务提供者的心跳数据
    HEARTBEAT_TO_PROVIDER(6);

    private final int type;

    RpcMessageTypeEnum(int type) {
        this.type = type;
    }

    public static RpcMessageTypeEnum findByType(int type) {
        for (RpcMessageTypeEnum rpcType : RpcMessageTypeEnum.values()) {
            if (rpcType.getType() == type) {
                return rpcType;
            }
        }
        return null;
    }

    public int getType() {
        return type;
    }
}
