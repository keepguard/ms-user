package com.keepguard.ms_user.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.application.dto.notify.NotifyViewDTO;
import com.keepguard.ms_user.application.port.out.cache.NotifyCachePort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotifyCacheService implements NotifyCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl.notify:2592000}")
    private long notifyTtlSeconds;

    @Value("${cache.redis.prefix.notify:notify_cache}")
    private String notifyCachePrefix;

    @CircuitBreaker(name = "redisCache")
    public void cacheNotifyByUserId(String userId, NotifyViewDTO notify) {
        try {
            String key = notifyKey(userId);
            String value = objectMapper.writeValueAsString(notify);
            redisTemplate.opsForValue().set(key, value, notifyTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear notificação por userId | key={}", userId);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getNotifyFallback")
    @Retry(name = "redisCache")
    public NotifyViewDTO getNotifyByUserIdFromCache(String userId) {
        var key = notifyKey(userId);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return null;
            }
            return objectMapper.readValue(value, NotifyViewDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private NotifyViewDTO getNotifyFallback(String userId, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

    @CircuitBreaker(name = "redisCache")
    public void removeNotifyFromCacheByUserId(String userId) {
        try {
            String key = notifyKey(userId);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover notificação do cache por userId | key={}", userId);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void clearAllNotifyCache() {
        try {
            var pattern = basePrefix() + ":*";
            var keys = redisTemplate.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                var deletedCount = redisTemplate.delete(keys);
                log.info("Cache de notificações limpo com sucesso. {} chave(s) removida(s)", deletedCount);
            } else {
                log.info("Nenhuma chave de cache de notificações encontrada para remover");
            }
        } catch (Exception e) {
            log.warn("Falha ao limpar cache de notificações");
        }
    }

    private String basePrefix() {
        if (notifyCachePrefix == null || notifyCachePrefix.isBlank()) {
            return "notify_cache";
        }
        return notifyCachePrefix.replaceAll(":+$", "");
    }

    private String notifyKey(String userId) {
        return basePrefix() + ":user:" + normalize(userId);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

}
