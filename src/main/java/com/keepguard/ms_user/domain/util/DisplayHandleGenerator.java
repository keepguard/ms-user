package com.keepguard.ms_user.domain.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Gera display_handle a partir do primeiro nome (ou fallback do e-mail).
 * Formato: [a-z0-9._-] com 3 a 64 caracteres.
 */
public final class DisplayHandleGenerator {

    private static final Pattern NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final int MIN_LEN = 3;
    private static final int MAX_LEN = 64;
    private static final String FALLBACK_BASE = "user";

    private DisplayHandleGenerator() {
    }

    /**
     * Base do handle: primeiro token do nome completo; se inválido, local-part do e-mail; senão {@code user}.
     */
    public static String baseFrom(String fullName, String email) {
        String fromName = slugFirstToken(fullName);
        if (fromName != null) {
            return fromName;
        }
        String fromEmail = slugEmailLocalPart(email);
        if (fromEmail != null) {
            return fromEmail;
        }
        return FALLBACK_BASE;
    }

    /**
     * {@code n == 1} → base; {@code n >= 2} → base + n (ex.: rafael, rafael2).
     */
    public static String withUniquenessSuffix(String base, int n) {
        String safeBase = (base == null || base.isBlank()) ? FALLBACK_BASE : base;
        if (n <= 1) {
            return truncate(safeBase, MAX_LEN);
        }
        String suffix = String.valueOf(n);
        int maxBase = MAX_LEN - suffix.length();
        if (maxBase < MIN_LEN) {
            maxBase = MIN_LEN;
        }
        return truncate(safeBase, maxBase) + suffix;
    }

    private static String slugFirstToken(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }
        String first = fullName.trim().split("\\s+")[0];
        return slug(first);
    }

    private static String slugEmailLocalPart(String email) {
        if (email == null || !email.contains("@")) {
            return null;
        }
        return slug(email.substring(0, email.indexOf('@')));
    }

    static String slug(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD);
        normalized = NON_ASCII.matcher(normalized).replaceAll("");
        normalized = normalized.toLowerCase().trim();
        normalized = normalized.replaceAll("[^a-z0-9._-]", "");
        normalized = normalized.replaceAll("^[._-]+|[._-]+$", "");
        if (normalized.length() > MAX_LEN) {
            normalized = normalized.substring(0, MAX_LEN);
        }
        if (normalized.length() < MIN_LEN) {
            return null;
        }
        return normalized;
    }

    private static String truncate(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }
}
