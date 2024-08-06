package com.musinsa.infra.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 실행 환경에 제약을 고려하여 로컬 메모리 사용
 * Redis Hash
 */
@Slf4j
@Repository
public class RedisRepository {
    private final Map<String, Map<String, Object>> redisStore = new ConcurrentHashMap<>();

    public void put(String key, String hashKey, Object value) {
        redisStore.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(hashKey, value);
    }

    public Object get(String key, String hashKey) {
        Map<String, Object> map = redisStore.get(key);
        if (map != null) {
            return map.get(hashKey);
        }
        return null;
    }

    public Map<String, Object> getAll(String key) {
        return redisStore.getOrDefault(key, new ConcurrentHashMap<>());
    }

    public void delete(String key, String hashKey) {
        Map<String, Object> map = redisStore.get(key);
        if (map != null) {
            map.remove(hashKey);
            if (map.isEmpty()) {
                redisStore.remove(key);
            }
        }
    }

    public void clear() {
        redisStore.clear();
        log.info("캐시 초기화 완료");
    }
}
