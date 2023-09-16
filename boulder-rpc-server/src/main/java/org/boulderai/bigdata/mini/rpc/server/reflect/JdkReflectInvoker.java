package org.boulderai.bigdata.mini.rpc.server.reflect;

import com.boulderai.bigdata.mini.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 文件描述
 *
 * @ProductName Xxx Company xxx
 * @ProjectName boulder-mini-rpc
 * @Package org.boulderai.bigdata.mini.rpc.server.reflect
 * @Description note
 * @Author wuxiaofei
 * @CreateDate 2023/8/27 4:07 PM
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2023 xxxCompany Technologies Inc. All Rights Reserved
 **/
@Slf4j
@SPIClass
public class JdkReflectInvoker implements ReflectInvoker {
    @Override
    public Object invokeMethod(Object serviceBean, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        log.info("use jdk reflect type invoke method...");
        Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }
}
