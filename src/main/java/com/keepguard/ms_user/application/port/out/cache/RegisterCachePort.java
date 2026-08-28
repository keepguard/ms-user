package com.keepguard.ms_user.application.port.out.cache;

import com.keepguard.ms_user.domain.entity.RegisterSession;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Optional;

public interface RegisterCachePort {

    /**
     * Salva uma sessão de registro no Redis.
     *
     * @param email Email do usuário
     * @param companyId UUID da empresa
     * @param session Sessão de registro
     * @throws JsonProcessingException se houver erro na serialização
     */
    void saveRegisterSession(String email, java.util.UUID companyId, RegisterSession session) throws JsonProcessingException;

    /**
     * Busca uma sessão de registro no Redis.
     *
     * @param email Email do usuário
     * @param companyId UUID da empresa
     * @return Sessão de registro se encontrada
     * @throws JsonProcessingException se houver erro na deserialização
     */
    Optional<RegisterSession> getRegisterSession(String email, java.util.UUID companyId) throws JsonProcessingException;

    /**
     * Remove uma sessão de registro do Redis.
     *
     * @param email Email do usuário
     * @param companyId UUID da empresa
     */
    void removeRegisterSession(String email, java.util.UUID companyId);

    /**
     * Verifica se existe uma sessão de registro no Redis.
     *
     * @param email Email do usuário
     * @param companyId UUID da empresa
     * @return true se existe, false caso contrário
     */
    boolean existsRegisterSession(String email, java.util.UUID companyId);
}

