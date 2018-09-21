package com.liuyj.core.cache;

import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyuanju1
 * @date 2018/9/21
 * @description:
 */
public class LocalCache {
    /**
     * 默认缓存时长 单位s
     */
    private static final int DEFAULT_TIMEOUT = 3600;
    /**
     * 默认缓存容量
     */
    private static final int DEFAULT_SIZE = 1000;

    /**
     * 存储数据
     */
    private static final Map<String,Object> data;

    /**
     * 定时器  用来控制 缓存的超时时间
     *     private static Timer timer;
     *     timer = new Timer();
     *     timer.schedule(CacheCleanTask.cacheTask(key),DEFAULT_TIMEOUT);
     *
     * 1)多线程并行处理定时任务时，Timer运行多个TimeTask时，只要其中之一没有捕获抛出的异常，
     * 其它任务便会自动终止运行，使用ScheduledExecutorService则没有这个问题
     * 2)Timer内部是一个线程，任务1所需的时间超过了两个任务间的间隔时会导致问题
     * 3)Timer执行周期任务时依赖系统时间
     */

    private static final ScheduledExecutorService executorService;

    //初始化
    static {
        data = new ConcurrentHashMap<>(DEFAULT_SIZE);
        executorService = new ScheduledThreadPoolExecutor(2);
    }
    /**
     * 定时器 调度任务，用于根据 时间 定时清除 对应key 缓存
     */
//    static class CacheCleanTask extends TimerTask {
//
//        private String key;
//
//        private CacheCleanTask(String key){
//            this.key = key;
//        }
//
//        public static CacheCleanTask cacheTask(String key){
//            return  new CacheCleanTask(key);
//        }
//
//        @Override
//        public void run() {
//            //移除对应 key
//            LocalCache.remove(key);
//        }
//    }

    /**
     * 私有化构造函数
     */
    private LocalCache(){}

    /**
     * 增加缓存 默认有效时长
     * @param key
     * @param value
     */
    public static void put(String key, Object value){
        data.put(key,value);
        //定时器 调度任务，用于根据 时间 定时清除 对应key 缓存
        executorService.schedule(new TimerTask() {
            @Override
            public void run() {
                remove(key);
            }
        }, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 增加缓存  并设置缓存时长 单位 s
     * @param key
     * @param value
     * @param timeout 缓存时长 单位s
     */
    public static void put(String key, Object value, int timeout){
        data.put(key, value);
        //lambda 替换匿名内部类
        executorService.schedule(() -> remove(key), timeout, TimeUnit.SECONDS);
    }

    /**
     * 增加缓存 并指定过期时间点
     * @param key
     * @param value
     * @param expireTime 指定过期时间点
     */
    public static void put(String key, Object value, LocalDateTime expireTime){
        data.put(key, value);
        LocalDateTime nowTime = LocalDateTime.now();
        if(nowTime.isAfter(expireTime)){
            //时间设置异常 待处理
        }
        long seconds = Duration.between(nowTime,expireTime).getSeconds();
        executorService.schedule(() -> remove(key), seconds, TimeUnit.SECONDS);
    }

    /**
     * 批量增加缓存
     * @param cache
     */
    public static void put(Map<String,Object> cache){
        if(!CollectionUtils.isEmpty(cache)){
            cache.entrySet().forEach(entry ->
                    put(entry.getKey(),entry.getValue()));
        }
    }

    public static void put(Map<String,Object> cache,int timeout){
        if(!CollectionUtils.isEmpty(cache)){
            cache.entrySet().forEach(entry ->
                    put(entry.getKey(),entry.getValue(),timeout));
        }
    }

    public static void put(Map<String,Object> cache,LocalDateTime expireTime){
        if(!CollectionUtils.isEmpty(cache)){
            cache.entrySet().forEach(entry ->
                    put(entry.getKey(),entry.getValue(),expireTime));
        }
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public static Object get(String key){
        return data.get(key);
    }

    /**
     * 获取当前缓存中 所有的key
     * @return
     */
    public static Set<String> cacheKeys(){
        return data.keySet();
    }

    public static Map<String,Object> allCache(){
        return data;
    }

    /**
     * 判断缓存是否包含key
     * @param key
     * @return
     */
    public boolean containKey(String key){
        return data.containsKey(key);
    }

    /**
     * 获取当前缓存大小
     * @return
     */
    public static int size(){
        return data.size();
    }

    /**
     * 删除缓存
     * @param key
     */
    public static void remove(String key){
        data.remove(key);
    }

    /**
     * 清空所有缓存
     */
    public static void clear(){
        if(size() > 0){
            data.clear();
        }
    }

}
