package com.footprint.test.jedis;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * jedis client哨兵连接测试
 */
public class RedisSentinelTest {

    // redis 采用1主1从方式， 主：127.0.0.1 6379 从：127.0.0.1 6380
    // sentinel 采用3哨兵， 127.0.0.1 16379 16380 16381
    public static void main(String[] args) {
        // 创建哨兵池
        Set<String> sentinels = new HashSet();
        //172.19.60.13:26379;172.19.60.44:26379
        sentinels.add(new HostAndPort("127.0.0.1", 16379).toString());
        sentinels.add(new HostAndPort("127.0.0.1", 16380).toString());
        sentinels.add(new HostAndPort("127.0.0.1", 16381).toString());
        /*sentinels.add(new HostAndPort("172.19.60.13", 26379).toString());
        sentinels.add(new HostAndPort("172.19.60.44", 26379).toString());*/
        JedisSentinelPool sentinelPool =
                new JedisSentinelPool("mymaster", sentinels, "123456");
        System.out.println("Current master: "
                + sentinelPool.getCurrentHostMaster().toString());

        Jedis master = sentinelPool.getResource();
        master.set("name", "zmy");
        System.out.println("set->name: zmy");
        sentinelPool.returnResource(master);

        // 这里休眠30秒 ,将6379 主redis杀掉，按哨兵机制，将发现主redis状态down
        // 重新选举新的slave为主master
        /*try {
            System.out.println("sleep 30s  begin");
            Thread.sleep(30000);
            System.out.println("sleep 30s  end!!!");
        } catch (Exception Exc) {
            Exc.printStackTrace();
            System.exit(0);
        }*/

        // 重新获得jedis
        Jedis master2 = sentinelPool.getResource();
        String value = master2.get("name");
        System.out.println("get->name: " + value);

        master2.set("name", "zh");
        System.out.println("set->name: zh ");

        String value2 = master2.get("name");
        System.out.println("get->name: " + value);

        master2.close();
        sentinelPool.destroy();
    }
}