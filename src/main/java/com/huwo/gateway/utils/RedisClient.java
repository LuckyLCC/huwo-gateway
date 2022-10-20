package com.huwo.gateway.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program:
 * @description:
 * @author: zhangAihua
 * @create_time: 2020/9/20 0:19
 */
@Service
@Slf4j
public class RedisClient {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T> void set(String key, T value) {
        String json = JSON.toJSONString(value);
        redisTemplate.opsForValue().set(key, json);
    }

    public void set(String key, String value, Long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public <T> void set(String key, T value, Long seconds) {
        String json = JSON.toJSONString(value);
        redisTemplate.opsForValue().set(key, json, seconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (null != value) {
            return value.toString();
        }
        return null;
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        String json = value.toString();
        return JSON.parseObject(json, clazz);
    }

    public <T> T get(String key, Type type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        String json = value.toString();
        return JSON.parseObject(json, type);
    }

    public <T> void sAdd(String key, T value) {
        String json = JSON.toJSONString(value);
        redisTemplate.opsForSet().add(key, json);
    }

    public <T> void sAdd(String key, T value, Long seconds) {
        String json = JSON.toJSONString(value);
        redisTemplate.opsForSet().add(key, json, seconds);
    }

    public boolean exist(String key) {
        Boolean aBoolean = redisTemplate.hasKey(key);
        return aBoolean != null;
    }

    public <T> Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public <T> Boolean isMember(String key, Object member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    public <T> void delete(String key) {
        redisTemplate.delete(key);
    }

    public <T> void hashSet(String key, String hashKey, T hashValue) {
        String json = JSON.toJSONString(hashValue);
        redisTemplate.opsForHash().put(key, hashKey, json);
    }

    public void hashSet(String key, String hashKey, String hashValue, Long seconds) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public void hashIncr(String key, String hashKey, Long hashValue){
        redisTemplate.opsForHash().increment(key, hashKey, hashValue);
    }

    public <T> T hashGet(String key, String hashKey, Class<T> hashValueClazz) {
        Object hashValue = redisTemplate.opsForHash().get(key, hashKey);
        if (hashValue == null) {
            return null;
        }
        String json = hashValue.toString();
        return JSON.parseObject(json, hashValueClazz);
    }

    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public <T> void hashDel(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}
