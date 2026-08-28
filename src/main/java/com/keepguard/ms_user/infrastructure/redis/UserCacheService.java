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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService implements UserCachePort {

    private static final String CONTEXT = "user";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl.user:2592000}")
    private long userTtlSeconds;

    @Value("${cache.redis.prefix.user:user_cache}")
    private String userCachePrefix;

    @CircuitBreaker(name = "redisCache")
    public void cacheUserById(String userId, UserDetailsViewDTO user) {
        try {
            String key = userKey("id", userId);
            String value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, value, userTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear usuário por ID | key={}", userId);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getUserFallback")
    @Retry(name = "redisCache")
    public UserDetailsViewDTO getUserByIdFromCache(String userId) {
        var key = userKey("id", userId);
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
            redisTemplate.delete(userKey("id", userId));
        } catch (Exception e) {
            log.warn("Falha ao remover usuário do cache por ID | key={}", userId);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void cacheUserByEmail(UUID companyId, String email, UserDetailsViewDTO user) {
        try {
            String key = emailCacheKey(companyId, email);
            String value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, value, userTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear usuário por email | key={}", email);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getUserByEmailFallback")
    @Retry(name = "redisCache")
    public UserDetailsViewDTO getUserByEmailFromCache(UUID companyId, String email) {
        var key = emailCacheKey(companyId, email);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) return null;
            return objectMapper.readValue(value, UserDetailsViewDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void removeUserFromCacheByEmail(UUID companyId, String email) {
        try {
            redisTemplate.delete(emailCacheKey(companyId, email));
        } catch (Exception e) {
            log.warn("Falha ao remover usuário do cache por email | key={}", email);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void cacheUserByCode(String codeUser, UserDetailsViewDTO user) {
        try {
            String key = userKey("codeuser", codeUser);
            String value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, value, userTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear usuário por codeUser | key={}", codeUser);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getUserFallback")
    @Retry(name = "redisCache")
    public UserDetailsViewDTO getUserByCodeFromCache(String codeUser) {
        var key = userKey("codeuser", codeUser);
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
            redisTemplate.delete(userKey("codeuser", codeUser));
        } catch (Exception e) {
            log.warn("Falha ao remover usuário do cache por codeUser | key={}", codeUser);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void removeUserFromCache(User user) {
        removeUserFromCacheById(user.getId().toString());
        removeUserFromCacheByEmail(user.getCompanyId(), user.getEmail());
        removeUserFromCacheByCode(user.getCodeUser().toString());
    }

    @CircuitBreaker(name = "redisCache")
    public void clearAllUserCache() {
        try {
            var pattern = basePrefix() + ":" + CONTEXT + ":*";
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

    private String basePrefix() {
        if (userCachePrefix == null || userCachePrefix.isBlank()) {
            return "user_cache";
        }
        return userCachePrefix.replaceAll(":+$", "");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String userKey(String lookup, String id) {
        return basePrefix() + ":" + CONTEXT + ":" + lookup + ":" + normalize(id);
    }

    private String emailCacheKey(UUID companyId, String email) {
        return basePrefix() + ":" + CONTEXT + ":email:" + companyId + ":" + normalize(email);
    }

    private UserDetailsViewDTO getUserByEmailFallback(UUID companyId, String email, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

    private UserDetailsViewDTO getUserFallback(String param, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

}
