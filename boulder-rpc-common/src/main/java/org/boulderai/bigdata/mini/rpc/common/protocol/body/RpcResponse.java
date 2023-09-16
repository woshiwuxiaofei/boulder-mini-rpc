package org.boulderai.bigdata.mini.rpc.common.protocol.body;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class RpcResponse extends RpcMessage {

    private static final long serialVersionUID = 425335064405584525L;

    private String error;
    private Object result;

    public boolean isError() {
        return StringUtils.isNotBlank(error);
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}