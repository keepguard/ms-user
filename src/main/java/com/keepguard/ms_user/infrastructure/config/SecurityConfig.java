package com.keepguard.ms_user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração de segurança para o microserviço ms-user.
 * Fornece beans necessários para criptografia de senhas e outras funcionalidades de segurança.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean para criptografia de senhas usando BCrypt.
     * BCrypt é um algoritmo de hash seguro e amplamente utilizado.
     * 
     * @return PasswordEncoder configurado com BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

