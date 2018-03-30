package com.footprint.redis.serializer;

import com.footprint.redis.container.CacheValueWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.footprint.utils.GsonUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * redis存储值Gson序列化
 * @author hui.zhou 17:01 2018/1/3
 */
public class GsonSerializer implements RedisSerializer<CacheValueWrapper> {
    private static final String CHARSET = "UTF-8";

    @Override
    public byte[] serialize(CacheValueWrapper valueContainer) throws SerializationException {
        try {
            return GsonUtils.buildGson().toJson(valueContainer).getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CacheValueWrapper deserialize(byte[] bytes) throws SerializationException {
        if(Objects.isNull(bytes)){
            return null;
        }
        try {
            String jsonValue = new String(bytes, CHARSET);
            JsonObject jsonEle = (JsonObject)GsonUtils.buildGson().fromJson(jsonValue, JsonElement.class);
            String className = jsonEle.get("className").getAsString();
            Class<?> clazz = Class.forName(className);
            CacheValueWrapper val = new CacheValueWrapper();
            val.setClassName(className);
            val.setValue(GsonUtils.buildGson().fromJson(jsonEle.get("value"), clazz));
            return val;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
