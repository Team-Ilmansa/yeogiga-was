package kr.co.yeogiga.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }

    public boolean existed(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setList(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public <T> void setListAll(String key, Collection<T> values) {
        redisTemplate.opsForList().rightPushAll(key, (Collection<Object>) values);
    }

    public void setValueInList(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    public void expire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        return objects.stream()
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    public <T> T getLastFromList(String key, Class<T> clazz) {
        List<Object> objects = redisTemplate.opsForList().range(key, -1, -1);
        if (objects == null || objects.isEmpty()) {
            return null;
        }
        return clazz.cast(objects.get(0));
    }

    public void removeFromList(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }

    public void addToSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void removeFromSet(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public boolean existsInSet(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void deleteKeys(Collection<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void setHash(String key, String subKey, Object value) {
        redisTemplate.opsForHash().put(key, subKey, value);
    }

    public void setHashExpire(String key, String subKey, Duration duration) {
        redisTemplate.opsForHash().expire(key, duration, List.of(subKey));
    }

    public Set<String> getHashKeys(String key) {
        return redisTemplate.opsForHash().keys(key).stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }
}
