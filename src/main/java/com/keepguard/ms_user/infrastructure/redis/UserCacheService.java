package com.keepguard.ms_user.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.application.port.out.cache.UserCachePort;
import com.keepguard.ms_user.domain.entity.User;
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
public class UserCacheService implements UserCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl.user:2592000}")
    private long userTtlSeconds;

    @Value("${cache.redis.prefix.user:user_cache}")
    private String userCachePrefix;

    @CircuitBreaker(name = "redisCache")
    public void cacheUserById(String userId, UserDetailsViewDTO user) {
        try {
            String key = userCachePrefix + ":id:" + userId;
            String value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, value, userTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear usuário por ID | key={}", userId);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getUserFallback")
    @Retry(name = "redisCache")
    public UserDetailsViewDTO getUserByIdFromCache(String userId) {
        var key = "%s:id:%s".formatted(userCachePrefix, userId);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return null;
            }
            return objectMapper.readValue(value, UserDetailsViewDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void removeUserFromCacheById(String userId) {
        try {
            String key = userCachePrefix + ":id:" + userId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover usuário do cache por ID | key={}", userId);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void cacheUserByEmail(String email, UserDetailsViewDTO user) {
        try {
            String key = userCachePrefix + ":email:" + email.toLowerCase().trim();
            String value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, value, userTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear usuário por email | key={}", email);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getUserFallback")
    @Retry(name = "redisCache")
    public UserDetailsViewDTO getUserByEmailFromCache(String email) {
        var key = "%s:email:%s".formatted(userCachePrefix, email);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) return null;
            return objectMapper.readValue(value, UserDetailsViewDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void removeUserFromCacheByEmail(String email) {
        try {
            String key = userCachePrefix + ":email:" + email.toLowerCase().trim();
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover usuário do cache por email | key={}", email);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void cacheUserByCode(String codeUser, UserDetailsViewDTO user) {
        try {
            String key = userCachePrefix + ":code:" + codeUser;
            String value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, value, userTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear usuário por codeUser | key={}", codeUser);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getUserFallback")
    @Retry(name = "redisCache")
    public UserDetailsViewDTO getUserByCodeFromCache(String codeUser) {
        var key = "%s:code:%s".formatted(userCachePrefix, codeUser);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) return null;
            return objectMapper.readValue(value, UserDetailsViewDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void removeUserFromCacheByCode(String codeUser) {
        try {
            String key = userCachePrefix + ":code:" + codeUser;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover usuário do cache por codeUser | key={}", codeUser);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void removeUserFromCache(User user) {
        removeUserFromCacheById(user.getId().toString());
        removeUserFromCacheByEmail(user.getEmail());
        removeUserFromCacheByCode(user.getCodeUser().toString());
    }

    @CircuitBreaker(name = "redisCache")
    public void clearAllUserCache() {
        try {
            var pattern = userCachePrefix + ":*";
            var keys = redisTemplate.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                var deletedCount = redisTemplate.delete(keys);
                log.info("Cache de usuários limpo com sucesso. {} chave(s) removida(s)", deletedCount);
            } else {
                log.info("Nenhuma chave de cache de usuários encontrada para remover");
            }
        } catch (Exception e) {
            log.warn("Falha ao limpar cache de usuários");
        }
    }

    private UserDetailsViewDTO getUserFallback(String param, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

}
