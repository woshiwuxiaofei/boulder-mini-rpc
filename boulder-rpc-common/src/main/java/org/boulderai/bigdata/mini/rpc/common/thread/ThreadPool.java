/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boulderai.bigdata.mini.rpc.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.boulderai.bigdata.mini.rpc.common.constants.RpcConstants;

@Slf4j
public class ThreadPool {

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 线程池
     */
    private static volatile ThreadPool instance;

    private ThreadPool(){

    }

    private ThreadPool(int corePoolSize, int maximumPoolSize){
        if (corePoolSize <= 0) {
            corePoolSize = RpcConstants.DEFAULT_CORE_POOL_SIZE;
        }
        if (maximumPoolSize <= 0) {
            maximumPoolSize = RpcConstants.DEFAULT_MAXI_NUM_POOL_SIZE;
        }
        if (corePoolSize > maximumPoolSize) {
            maximumPoolSize = corePoolSize;
        }
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, RpcConstants.DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(RpcConstants.DEFAULT_QUEUE_CAPACITY));
    }

    /**
     * 单例传递参数创建对象，只以第一次传递的参数为准
     */
    public static ThreadPool getInstance(int corePoolSize, int maximumPoolSize){
        if (instance == null){
            synchronized (ThreadPool.class){
                if (instance == null){
                    instance = new ThreadPool(corePoolSize, maximumPoolSize);
                }
            }
        }
        return instance;
    }


    public void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public <T> T submit(Callable<T> task){
        Future<T> future = threadPoolExecutor.submit(task);
        try {
            return future.get();
        } catch (Exception e) {
            log.error("submit callable task exception:{}", e.getMessage());
        }
        return null;
    }

    public void stop(){
        threadPoolExecutor.shutdown();
    }
}
