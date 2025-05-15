package kr.co.yeogiga.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
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

    public void setValueInList(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
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
}
