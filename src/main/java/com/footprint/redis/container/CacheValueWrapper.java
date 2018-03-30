package com.footprint.redis.container;

import java.io.Serializable;

/**
 * 缓存对象包装类
 * @author hui.zhou 17:25 2018/1/3
 */
public class CacheValueWrapper<T> implements Serializable {
    private T value;
    private String className;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
