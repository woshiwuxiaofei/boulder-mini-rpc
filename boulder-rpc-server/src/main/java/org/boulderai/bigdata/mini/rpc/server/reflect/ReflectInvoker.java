package org.boulderai.bigdata.mini.rpc.server.reflect;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server.reflect
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/27 4:10 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
public interface ReflectInvoker {

    public Object invokeMethod(Object serviceBean, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable;

}
