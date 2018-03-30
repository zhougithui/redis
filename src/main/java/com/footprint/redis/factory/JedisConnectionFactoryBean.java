package com.footprint.redis.factory;

import com.footprint.redis.RedisUtils;
import com.footprint.redis.container.CacheValueWrapper;
import com.footprint.redis.serializer.GsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * 封装jedis初始化过程
 * @author hui.zhou 8:50 2018/1/23
 */
public class JedisConnectionFactoryBean implements FactoryBean<JedisConnectionFactory> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 哨兵，以 ; 分隔
     * 127.0.0.1:16379;127.0.0.1:16380;127.0.0.1:16381
     */
    private String sentinels;

    private String masterName;
    /**
     * 密码
     */
    private String password = null;

    private int database = 0;

    private long defaultExpiredSeconds = 10;

    private long timeout = 100000;

    /**
     * 最大空闲连接数
     */
    private int maxIdle = 10;

    /**
     * 最大连接数
     */
    private int maxActive = 30;

    /**
     * 获取连接时的最大等待毫秒数,小于零:阻塞不确定的时间,默认-1
     */
    private int maxWait = 3000;

    /**
     * 在获取连接的时候检查有效性, 默认false
     */
    private boolean testOnBorrow = true;

    private boolean useSsl = false;
    private boolean usePooling = true;
    private String clientName = "redis-zmy";
    private long connectTimeout = 10000;
    private long readTimeout = 10000;

    public String getSentinels() {
        return sentinels;
    }

    public void setSentinels(String sentinels) {
        this.sentinels = sentinels;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public long getDefaultExpiredSeconds() {
        return defaultExpiredSeconds;
    }

    public void setDefaultExpiredSeconds(long defaultExpiredSeconds) {
        this.defaultExpiredSeconds = defaultExpiredSeconds;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public boolean isUsePooling() {
        return usePooling;
    }

    public void setUsePooling(boolean usePooling) {
        this.usePooling = usePooling;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    private RedisTemplate<String, CacheValueWrapper<Object>> redisTemplate = new RedisTemplate<>();

    @Nullable
    @Override
    public JedisConnectionFactory getObject() throws Exception {
        //哨兵配置初始化
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.setMaster(getMasterName());
        try {
            Set<RedisNode> sentinelSet = new HashSet<>();
            for (String node : getSentinels().split(";")){
                RedisNode redisNode = new RedisNode(node.split(":")[0], Integer.parseInt(node.split(":")[1]));
                sentinelSet.add(redisNode);
            }

            redisSentinelConfiguration.setSentinels(sentinelSet);
        } catch (Exception e) {
            logger.error("redis哨兵节点初始化失败", e);
            throw new IllegalArgumentException(e);
        }
        redisSentinelConfiguration.setDatabase(getDatabase());
        redisSentinelConfiguration.setPassword(RedisPassword.of(getPassword()));

        /**
         * jedis pool配置
         */
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(getMaxIdle());
        jedisPoolConfig.setMaxTotal(getMaxActive());
        jedisPoolConfig.setMaxWaitMillis(getMaxWait());
        jedisPoolConfig.setTestOnBorrow(isTestOnBorrow());

        DefaultJedisClientConfiguration jedisClientConfiguration = new DefaultJedisClientConfiguration(
                isUseSsl(),
                null,
                null,
                null,
                isUsePooling(),
                jedisPoolConfig,
                getClientName(),
                Duration.ofSeconds(getReadTimeout()),
                Duration.ofSeconds(getConnectTimeout())
                );

        //初始化jedis连接工厂
        JedisConnectionFactory jedisConnectionFactory
                = new JedisConnectionFactory(redisSentinelConfiguration, jedisClientConfiguration);
        jedisConnectionFactory.afterPropertiesSet();

        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setDefaultSerializer(new GsonSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        RedisUtils.setRedisTemplate(redisTemplate);
        RedisUtils.setExpireTime(getDefaultExpiredSeconds());


        logger.info("redis connection factory init end");
        return jedisConnectionFactory;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return JedisConnectionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
