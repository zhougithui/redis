package com.footprint.redis;

import com.footprint.redis.container.CacheValueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis缓存工具类
 * @author hui.zhou 17:28 2018/1/3
 */
@SuppressWarnings("ALL")
public class RedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static RedisTemplate<String, CacheValueWrapper<Object>> redisTemplate;
    private static long DEFAULT_EXPIRE_SECONDS = 10000;
    private static TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    public static RedisTemplate<String, CacheValueWrapper<Object>> getRedisTemplate() {
        return redisTemplate;
    }

    public static void setRedisTemplate(RedisTemplate<String, CacheValueWrapper<Object>> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 设置默认的过期时间
     * @param expireTime
     */
    public static void setExpireTime(long expireTime) {
        RedisUtils.DEFAULT_EXPIRE_SECONDS = expireTime;
    }

    /**
     * key对应value
     * @param key 键
     * @param value 值
     */
    public static <T> void set(String key, T value){
        try {
            redisTemplate.opsForValue().set(key, wrapper(value));
            expire(key, DEFAULT_EXPIRE_SECONDS, DEFAULT_UNIT);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * key对应value
     * @param key 键
     * @param value 值
     * @param expireTime 过期时间
     * @param unit 时间单位
     */
    public static <T> void set(String key, T value, long expireTime, TimeUnit unit){
        try {
            redisTemplate.opsForValue().set(key, wrapper(value));
            expire(key, expireTime, unit);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public static <T> T get(String key, Class<T> cls){
        CacheValueWrapper<Object> value;
        try {
            value = redisTemplate.opsForValue().get(key);
            validateType(value, cls);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
            return null;
        }
        CacheValueWrapper<T> val = (CacheValueWrapper<T>)value;
        return Objects.isNull(val) ? null : val.getValue();
    }

    /**
     * key对应的list增加元素
     * @param key 缓存key
     * @param value 缓存对象
     */
    public static <T> void lput(String key, T value){
        try {
            redisTemplate.opsForList().leftPush(key, wrapper(value));
            expire(key, DEFAULT_EXPIRE_SECONDS, DEFAULT_UNIT);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * key对应的list增加元素
     * @param key 缓存key
     * @param value 缓存对象
     */
    public static <T> void lput(String key, T value, long expireTime, TimeUnit unit){
        try {
            redisTemplate.opsForList().leftPush(key, wrapper(value));
            expire(key, expireTime, unit);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * key对应的list增加元素
     * @param key 缓存key
     * @param values 缓存对象集合
     */
    public static <T> void lputAll(String key, List<T> values){
        try {
            redisTemplate.opsForList().rightPushAll(key,
                    values.stream()
                    .map(value -> wrapper(value))
                    .collect(Collectors.toCollection(ArrayList::new)));
            expire(key, DEFAULT_EXPIRE_SECONDS, DEFAULT_UNIT);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * key对应的list增加元素
     * @param key 缓存key
     * @param values 缓存对象集合
     * @param expireTime 过期时间
     * @param unit 时间单位
     */
    public static <T> void lputAll(String key, List<T> values, long expireTime, TimeUnit unit){
        try {
            redisTemplate.opsForList().rightPushAll(key,
                    values.stream()
                    .map(value -> wrapper(value))
                    .collect(Collectors.toCollection(ArrayList::new)));
            expire(key, expireTime, unit);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * 查询出key对应的list集合
     * @param key redis缓存的key
     * @param cls 缓存的实际对象类型
     * @return 缓存结果列表
     */
    public static <T> List<T> lgetAll(String key, Class<T> cls){
        try {
            long size = redisTemplate.opsForList().size(key);
            if(size == 0){
                return null;
            }
            List<CacheValueWrapper<Object>> list = redisTemplate.opsForList().range(key, 0, size);
            return (List<T>)list.stream().map( value -> {
                validateType(value, cls);
              return value.getValue();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
            return null;
        }
    }

    /**
     * hash缓存
     * @param key 键
     * @param value 键值对
     */
    public static <T> void hmset(String key, Map<String, T> value){
        try {
            Map<String, CacheValueWrapper<Object>> map = new HashMap<>();
            value.forEach((k, v) -> map.put(k, wrapper(v)));
            redisTemplate.opsForHash().putAll(key, map);

            expire(key, DEFAULT_EXPIRE_SECONDS, DEFAULT_UNIT);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * hash缓存
     * @param key 键
     * @param value 键值对
     */
    public static <T> void hmset(String key, Map<String, T> value, long expireTime, TimeUnit unit){
        try {
            Map<String, CacheValueWrapper<Object>> map = new HashMap<>();
            value.forEach((k, v) -> map.put(k, wrapper(v)));
            redisTemplate.opsForHash().putAll(key, map);

            expire(key, expireTime, unit);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * 通过key获取map
     * @param key
     * @param cls
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> hgetAll(String key, Class<V> cls){
        try {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);

            Map<K, V> result = new HashMap<>();
            map.forEach((k, v) -> {
                validateType((CacheValueWrapper<Object>) v, cls);
                result.put((K)k, ((CacheValueWrapper<V>)v).getValue());
            });
            return result;
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
            return null;
        }
    }

    /**
     * set集合增加数据
     * @param key
     * @param value
     */
    public static <T> void sadd(String key, T value){
        try {
            redisTemplate.opsForSet().add(key, wrapper(value));
            expire(key, DEFAULT_EXPIRE_SECONDS, DEFAULT_UNIT);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * set集合增加数据
     * @param key
     * @param value
     */
    public static <T> void sadd(String key, T value, long expireTime, TimeUnit unit){
        try {
            redisTemplate.opsForSet().add(key, wrapper(value));
            expire(key, expireTime, unit);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * set集合增加数据
     * @param key
     * @param values
     */
    public static <T> void saddAll(String key, Set<T> values){
        try {
            redisTemplate.opsForSet().add(key,
                    values.stream()
                            .map(value -> wrapper(value))
                            .collect(Collectors.toList())
                            .toArray(new CacheValueWrapper[]{})
            );
            expire(key, DEFAULT_EXPIRE_SECONDS, DEFAULT_UNIT);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * set集合增加数据
     * @param key
     * @param values
     */
    public static <T> void saddAll(String key, Set<T> values, long expireTime, TimeUnit unit){
        try {
            redisTemplate.opsForSet().add(key,
                    values.stream()
                            .map(value -> wrapper(value))
                            .collect(Collectors.toList())
                            .toArray(new CacheValueWrapper[]{})
            );
            expire(key, expireTime, unit);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * 获取集合所有元素
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> Set<T> smenbers(String key, Class<T> cls){
        try {
            return (Set<T>)redisTemplate.opsForSet().members(key)
                    .stream()
                    .map(val -> {
                        validateType(val, cls);
                        return val.getValue();
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
            return null;
        }
    }


    //-------------------------------------
    //---------------公用-----------------
    //-------------------------------------
    /**
     * 删除key对应的缓存
     * @param key
     */
    public static void removeKey(String key){
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
        }
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public static boolean exists(String key){
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    try {
                        return connection.exists(key.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        return Boolean.FALSE;
                    }
                }
            });
        } catch (Exception e) {
            logger.error("redis缓存操作失败", e);
            return Boolean.FALSE;
        }
    }

    /**
     * 设置key过期时间
     * @param key
     * @param expireTime
     * @param unit
     */
    private static void expire(String key, long expireTime, TimeUnit unit){
        redisTemplate.expire(key, expireTime, unit);
    }

    /**
     * 缓存对象封装
     * @param value
     * @return
     */
    private static CacheValueWrapper<Object> wrapper(Object value){
        CacheValueWrapper<Object> val = new CacheValueWrapper<>();
        val.setValue(value);
        val.setClassName(value.getClass().getName());
        return val;
    }

    /**
     * 验证要求类型与实际类型是否一致
     * @param val
     * @param cls
     * @param className
     * @param <T>
     */
    private static <T> void validateType(CacheValueWrapper<Object> cacheVal, Class<T> cls) {
        if(("int".equals(cls.getName()) && Integer.class.isInstance(cacheVal.getValue()))
                || ("double".equals(cls.getName()) && Double.class.isInstance(cacheVal.getValue()))
                || ("float".equals(cls.getName()) && Float.class.isInstance(cacheVal.getValue()))
                || ("byte".equals(cls.getName()) && Byte.class.isInstance(cacheVal.getValue()))
                || ("boolean".equals(cls.getName()) && Boolean.class.isInstance(cacheVal.getValue()))
                || ("long".equals(cls.getName()) && Long.class.isInstance(cacheVal.getValue()))
                || ("short".equals(cls.getName()) && Short.class.isInstance(cacheVal.getValue()))
            ){

        }else if(!cls.isInstance(cacheVal.getValue())){
            throw new IllegalArgumentException("类型不匹配,需要序列化的class="
                    + cls.getName() + ",实际数据class=" + cacheVal.getClassName());
        }
    }
}
