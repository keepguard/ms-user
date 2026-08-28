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
    public void saveRegisterSession(String email, UUID companyId, RegisterSession session) throws com.fasterxml.jackson.core.JsonProcessingException {
        String key = buildKey(email, companyId);
        String value = objectMapper.writeValueAsString(session);
        
        redisTemplate.opsForValue().set(key, value, registerSessionTtlSeconds, java.util.concurrent.TimeUnit.SECONDS);
        log.info("Sessão de registro salva no cache: key={}, email={}, companyId={}, ttl={}s", 
                key, email, companyId, registerSessionTtlSeconds);
    }

    @Override
    @Retry(name = "registerCache")
    public Optional<RegisterSession> getRegisterSession(String email, UUID companyId) throws com.fasterxml.jackson.core.JsonProcessingException {
        String key = buildKey(email, companyId);
        String value = redisTemplate.opsForValue().get(key);
        
        if (value == null || value.isBlank()) {
            log.debug("Sessão de registro não encontrada no cache: key={}, email={}, companyId={}", 
                    key, email, companyId);
            return Optional.empty();
        }
        
        RegisterSession session = objectMapper.readValue(value, RegisterSession.class);
        log.debug("Sessão de registro encontrada no cache: key={}, email={}, companyId={}", 
                key, email, companyId);
        return Optional.of(session);
    }

    @Override
    @Retry(name = "registerCache")
    public void removeRegisterSession(String email, UUID companyId) {
        String key = buildKey(email, companyId);
        redisTemplate.delete(key);
        log.info("Sessão de registro removida do cache: key={}, email={}, companyId={}", 
                key, email, companyId);
    }

    @Override
    @Retry(name = "registerCache")
    public boolean existsRegisterSession(String email, UUID companyId) {
        String key = buildKey(email, companyId);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        log.debug("Verificação de existência de sessão no cache: key={}, email={}, companyId={}, exists={}", 
                key, email, companyId, exists);
        return exists;
    }

    private String buildKey(String email, UUID companyId) {
        return String.format("%s:%s:%s", basePrefix(), email == null ? "" : email.toLowerCase().trim(), companyId);
    }

    private String basePrefix() {
        if (registerCachePrefix == null || registerCachePrefix.isBlank()) {
            return "register_session";
        }
        return registerCachePrefix.replaceAll(":+$", "");
    }

}

