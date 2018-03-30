package com.footprint.test.redis;

import com.footprint.redis.RedisUtils;
import com.footprint.test.redis.vo.Student;
import com.footprint.utils.GsonUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtils工具类单元测试
 * @author hui.zhou 14:26 2018/1/3
 */
public class RedisUtilsTest {
    static ClassPathXmlApplicationContext ac;

    @BeforeClass
    public static void init(){
        ac = new ClassPathXmlApplicationContext("classpath:spring-redis.xml");
        ac.registerShutdownHook();
        ac.getBean(JedisConnectionFactory.class);
    }

    @AfterClass
    public static void close(){
        ac.close();
    }

    /**
     * 基础数据类型测试
     */
    @Test
    public void testBaseTypes(){
        RedisUtils.set("int", 1);
        Assert.isTrue(1 == RedisUtils.get("int", int.class), "缓存数据获取失败");
        RedisUtils.set("byte", (byte)1);
        Assert.isTrue(1 == RedisUtils.get("byte", byte.class), "缓存数据获取失败");
        RedisUtils.set("short", (short)1);
        Assert.isTrue(1 == RedisUtils.get("short", short.class), "缓存数据获取失败");
        RedisUtils.set("long", 1l);
        Assert.isTrue(1 == RedisUtils.get("long", long.class), "缓存数据获取失败");
        RedisUtils.set("double", 1d);
        Assert.isTrue(1 == RedisUtils.get("double", double.class), "缓存数据获取失败");
        RedisUtils.set("float", 1f);
        Assert.isTrue(1 == RedisUtils.get("float", float.class), "缓存数据获取失败");
        RedisUtils.set("boolean", true);
        Assert.isTrue(RedisUtils.get("boolean", boolean.class), "缓存数据获取失败");
        RedisUtils.set("String", "abc");
        Assert.isTrue("abc".equals(RedisUtils.get("String", String.class)), "缓存数据获取失败");
    }

    /**
     * 对象 string操作
     * @throws IOException
     */
    @Test
    public void testObj() throws IOException {
        Student zmy = new Student();
        zmy.setAge(20);
        zmy.setBirthday(new Date());
        zmy.setName("zmy");
        zmy.setScore(BigDecimal.valueOf(59.5));

        RedisUtils.set(zmy.getName(), zmy);
        Assert.isInstanceOf(Student.class, RedisUtils.get(zmy.getName(), Student.class));
    }

    /**
     * set集合测试
     */
    @Test
    public void testSet(){
        Student zmy = new Student();
        zmy.setAge(20);
        zmy.setBirthday(new Date());
        zmy.setName("zmy");
        zmy.setScore(BigDecimal.valueOf(59.5));

        Set<Student> studentSet = new HashSet<>();
        studentSet.add(zmy);
        RedisUtils.saddAll(zmy.getName()+ "" + zmy.getAge(), studentSet);
        RedisUtils.smenbers(zmy.getName(), Student.class).forEach(val -> System.out.println(zmy.getScore()));
    }

    /**
     * list列表操作
     */
    @Test
    public void testList(){
        Student zmy = new Student();
        zmy.setAge(20);
        zmy.setBirthday(new Date());
        zmy.setName("zmy");
        zmy.setScore(BigDecimal.valueOf(59.5));

        RedisUtils.lputAll("lstus", Arrays.asList(zmy));
        List<Student> infoList = RedisUtils.lgetAll("lstus", Student.class);
        infoList.forEach(stu -> System.out.println(GsonUtils.buildGson().toJson(stu)));
    }

    /**
     * hash操作
     */
    @Test
    public void testHash(){
        Student zmy = new Student();
        zmy.setAge(20);
        zmy.setBirthday(new Date());
        zmy.setName("zmy");
        zmy.setScore(BigDecimal.valueOf(59.5));

        Map<String, Student> map = new HashMap<>();
        map.put(zmy.getName(), zmy);
        RedisUtils.hmset("maps", map);

        Map<String, Student> result = RedisUtils.hgetAll("maps", Student.class);
        Assert.isTrue(result.get(zmy.getName()).getName().equals("zmy"), "缓存查询失败");
    }

    /**
     * 默认过期时间10秒
     */
    @Test
    public void testExpireTime(){
        RedisUtils.set("expire", "abcd");
        int seconds = 0;
        while (seconds <= 10){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            seconds++;
        }
        Assert.isNull(RedisUtils.get("expire", String.class), "默认过去设置未生效");
    }

    @Test
    public void testUpdate(){
        Student zmy = new Student();
        zmy.setAge(10);
        zmy.setBirthday(new Date());
        zmy.setName("zh");
        zmy.setScore(BigDecimal.valueOf(59.5));

        Map<String, Student> map = new HashMap<>();
        map.put(zmy.getName(), zmy);
        RedisUtils.hmset("maps", map);

        Map<String, Student> result = RedisUtils.hgetAll("maps", Student.class);
        Assert.isTrue(result.get(zmy.getName()).getName().equals("zh"), "缓存查询失败");

        zmy.setAge(20);
        RedisUtils.hmset("maps", map);
        result = RedisUtils.hgetAll("maps", Student.class);
        Assert.isTrue(result.get(zmy.getName()).getAge() == 20, "缓存更新失败");
    }
}
