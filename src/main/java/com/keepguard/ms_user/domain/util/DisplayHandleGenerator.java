package com.keepguard.ms_user.domain.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utilitário para geração de display_handle a partir de full_name
 * 
 * Segue as regras:
 * - Formato: [a-z0-9._-] (min 3, max 64 caracteres)
 * - Normalização: lowercase, remoção de acentos
 * - Para nomes com espaços: primeiro.ultimo
 * - Para nomes sem espaços: nome normalizado
 * 
 * @author KeepGuard Team
 * @version 1.0
 * @since 2025-01-25
 */
public final class DisplayHandleGenerator {
    
    private static final Pattern NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    
    private DisplayHandleGenerator() {
        // Utility class - não instanciar
    }
    
    /**
     * Gera display_handle a partir de full_name
     * 
     * @param fullName Nome completo do usuário
     * @return display_handle normalizado ou null se fullName for inválido
     */
    public static String generateFromFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }
        
        // Normalizar e remover acentos
        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD);
        normalized = NON_ASCII.matcher(normalized).replaceAll("");
        
        // Converter para lowercase e substituir espaços múltiplos por ponto único
        normalized = normalized.toLowerCase().trim();
        normalized = normalized.replaceAll("\\s+", ".");
        
        // Remover caracteres inválidos, manter apenas a-z, 0-9, . e -
        normalized = normalized.replaceAll("[^a-z0-9.-]", "");
        
        // Limitar tamanho a 64 caracteres
        if (normalized.length() > 64) {
            normalized = normalized.substring(0, 64);
        }
        
        // Remover pontos/hífens no início/fim
        normalized = normalized.replaceAll("^[.-]+|[.-]+$", "");
        
        // Se resultar em string vazia ou menor que 3 caracteres, retornar null
        if (normalized.isEmpty() || normalized.length() < 3) {
            return null;
        }
        
        return normalized;
    }
}
