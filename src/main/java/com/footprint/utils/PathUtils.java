package com.footprint.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 工程路径工具类
 * @author hui.zhou 9:10 2018/1/25
 */
public final class PathUtils {
    public static void main(String[] args) {
        Arrays.stream(StringUtils.split(classPathOfAll(), ";"))
                .forEach(System.out::println);
    }

    /**
     * 获取classpath路径
     * D:/worksoft/IntelliJIDEA/workspace/footprint-zh/footprint-zh-common/target/classes
     * @return
     */
    public static String rootClassPath(){
        return PathUtils.class.getResource("/").getPath().substring(1);
    }

    /**
     * 获取classpath url路径
     * file:/D:/worksoft/IntelliJIDEA/workspace/footprint-zh/footprint-zh-common/target/classes/
     * @return
     */
    public static URL rootClassPathURL(){
        return PathUtils.class.getResource("/");
    }

    /**
     * 获取指定类路径
     * /D:/worksoft/IntelliJIDEA/workspace/footprint-zh/footprint-zh-common/target/classes/com/zmy/utils/
     * @param cls
     * @return
     */
    public static String pathOfClass(Class<?> cls){
        return cls.getResource("").getPath().substring(1);
    }

    /**
     * 获取项目根路径
     * D:\worksoft\IntelliJIDEA\workspace\footprint-zh
     * @return
     */
    public static String projectPath(){
        File directory = new File("");// 参数为空
        try {
            return directory.getCanonicalPath();
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 获取项目根路径
     * D:\worksoft\IntelliJIDEA\workspace\footprint-zh
     * @return
     */
    public static String projectPath2(){
        return System.getProperty("user.dir");
    }

    /**
     * D:\worksoft\Java\jdk8\jre\lib\charsets.jar;
     * D:\worksoft\Java\jdk8\jre\lib\deploy.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\access-bridge-64.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\cldrdata.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\dnsns.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\jaccess.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\jfxrt.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\localedata.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\nashorn.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\sunec.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\sunjce_provider.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\sunmscapi.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\sunpkcs11.jar;
     * D:\worksoft\Java\jdk8\jre\lib\ext\zipfs.jar;
     * D:\worksoft\Java\jdk8\jre\lib\javaws.jar;
     * D:\worksoft\Java\jdk8\jre\lib\jce.jar;
     * D:\worksoft\Java\jdk8\jre\lib\jfr.jar;
     * D:\worksoft\Java\jdk8\jre\lib\jfxswt.jar;
     * D:\worksoft\Java\jdk8\jre\lib\jsse.jar;
     * D:\worksoft\Java\jdk8\jre\lib\management-agent.jar;
     * D:\worksoft\Java\jdk8\jre\lib\plugin.jar;
     * D:\worksoft\Java\jdk8\jre\lib\resources.jar;
     * D:\worksoft\Java\jdk8\jre\lib\rt.jar;
     * D:\worksoft\IntelliJIDEA\workspace\footprint-zh\footprint-zh-common\target\classes;
     * D:\worksoft\maven321\repo\junit\junit\4.12\junit-4.12.jar;
     * D:\worksoft\maven321\repo\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;
     * D:\worksoft\maven321\repo\org\slf4j\jcl-over-slf4j\1.7.2\jcl-over-slf4j-1.7.2.jar;
     * D:\worksoft\maven321\repo\ch\qos\logback\logback-classic\1.1.7\logback-classic-1.1.7.jar;
     * D:\worksoft\maven321\repo\ch\qos\logback\logback-core\1.1.7\logback-core-1.1.7.jar;
     * D:\worksoft\maven321\repo\com\google\code\gson\gson\2.2.4\gson-2.2.4.jar;
     * D:\worksoft\IntelliJIDEA\lib\idea_rt.jar
     * @return 当前所有类路径，包含jar包
     */
    public static String classPathOfAll(){
        return System.getProperty("java.class.path");
    }

    /**
     * 包名转换成路径
     * @param cls
     * @return
     */
    public static String packageToPath(Class<?> cls){
        return Arrays.stream(cls.getName().split("\\.")).collect(Collectors.joining(File.separator));
    }

}
