package com.keepguard.ms_user.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.application.port.out.cache.RegisterCachePort;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCacheService implements RegisterCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl.register_session:1200}")
    private long registerSessionTtlSeconds;

    @Value("${cache.redis.prefix.register:register_session}")
    private String registerCachePrefix;

    @Override
    @Retry(name = "registerCache")
    public void saveRegisterSession(String email, UUID xApplication, RegisterSession session) throws com.fasterxml.jackson.core.JsonProcessingException {
        String key = buildKey(email, xApplication);
        String value = objectMapper.writeValueAsString(session);
        
        redisTemplate.opsForValue().set(key, value, registerSessionTtlSeconds, java.util.concurrent.TimeUnit.SECONDS);
        log.info("Sessão de registro salva no cache: key={}, email={}, xApplication={}, ttl={}s", 
                key, email, xApplication, registerSessionTtlSeconds);
    }

    @Override
    @Retry(name = "registerCache")
    public Optional<RegisterSession> getRegisterSession(String email, UUID xApplication) throws com.fasterxml.jackson.core.JsonProcessingException {
        String key = buildKey(email, xApplication);
        String value = redisTemplate.opsForValue().get(key);
        
        if (value == null || value.isBlank()) {
            log.debug("Sessão de registro não encontrada no cache: key={}, email={}, xApplication={}", 
                    key, email, xApplication);
            return Optional.empty();
        }
        
        RegisterSession session = objectMapper.readValue(value, RegisterSession.class);
        log.debug("Sessão de registro encontrada no cache: key={}, email={}, xApplication={}", 
                key, email, xApplication);
        return Optional.of(session);
    }

    @Override
    @Retry(name = "registerCache")
    public void removeRegisterSession(String email, UUID xApplication) {
        String key = buildKey(email, xApplication);
        redisTemplate.delete(key);
        log.info("Sessão de registro removida do cache: key={}, email={}, xApplication={}", 
                key, email, xApplication);
    }

    @Override
    @Retry(name = "registerCache")
    public boolean existsRegisterSession(String email, UUID xApplication) {
        String key = buildKey(email, xApplication);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        log.debug("Verificação de existência de sessão no cache: key={}, email={}, xApplication={}, exists={}", 
                key, email, xApplication, exists);
        return exists;
    }

    private String buildKey(String email, UUID xApplication) {
        return String.format("%s:%s:%s", registerCachePrefix, email.toLowerCase().trim(), xApplication);
    }

}

