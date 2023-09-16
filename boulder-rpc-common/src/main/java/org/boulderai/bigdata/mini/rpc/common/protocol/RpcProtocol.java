package org.boulderai.bigdata.mini.rpc.common.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import org.boulderai.bigdata.mini.rpc.common.protocol.header.RpcHeader;


@Getter
@Setter
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = 292789485166173277L;

    /**
     * 消息头
     */
    private RpcHeader header;
    /**
     * 消息体
     */
    private T body;
}
