package com.footprint.utils;

/**
 * 对StringBuilder封装
 * @author zhou.hui 2017-07-28 11:28:29
 */
public final class FpStringBuilder {

    private StringBuilder sb;

    /**
     * 创建initCapacity为50的builder
     * @return
     */
    public static FpStringBuilder buildDefault(){
        FpStringBuilder builder = new FpStringBuilder();
        builder.sb = new StringBuilder(50);
        return builder;
    }

    /**
     * 创建指定initCapacity的builder
     * @param initCapacity
     * @return
     */
    public static FpStringBuilder build(int initCapacity){
        if(initCapacity <= 0){
            throw new IllegalArgumentException("初始化容量必须大于0");
        }
        FpStringBuilder builder = new FpStringBuilder();
        builder.sb = new StringBuilder(initCapacity);
        return builder;
    }

    public FpStringBuilder append(Object msg){
        this.sb.append(msg);
        return this;
    }

    public String toString(){
        return this.sb.toString();
    }
}
