package com.keepguard.ms_user.domain.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Interface selada que define os tipos possíveis de perfil de usuário.
 * Garante type safety e permite pattern matching exhaustivo.
 * 
 * <p>Esta interface usa o recurso de sealed interfaces do Java 17+ para
 * garantir que apenas os tipos conhecidos (PersonProfile e CompanyProfile)
 * possam implementá-la, proporcionando segurança de tipo em tempo de compilação.</p>
 * 
 * @see PersonProfile
 * @see CompanyProfile
 */
public sealed interface UserProfile permits PersonProfile, CompanyProfile {
    
    /**
     * Retorna o ID do usuário associado ao perfil
     * 
     * @return UUID do usuário
     */
    UUID getUserId();
    
    /**
     * Retorna a data de criação do perfil
     * 
     * @return OffsetDateTime da criação
     */
    OffsetDateTime getCreatedAt();
    
    /**
     * Retorna a data da última atualização do perfil
     * 
     * @return OffsetDateTime da última atualização
     */
    OffsetDateTime getUpdatedAt();
}

