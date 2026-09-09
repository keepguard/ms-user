package com.keepguard.ms_user.application.service.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;

@Getter
@Slf4j
public class UnprocessableException extends RuntimeException {

    private final String errorCode;
    private final Map<String, Object> context;

    public UnprocessableException(String message, String errorCode, Map<String, Object> context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context != null ? context : Map.of();
        logStructuredError();
    }

    public UnprocessableException(String message, String errorCode) {
        this(message, errorCode, Map.of());
    }

    private void logStructuredError() {
        MDC.put("errorCode", errorCode);
        MDC.put("exceptionType", this.getClass().getSimpleName());

        if (context != null) {
            context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
        }

        log.warn("Dados não processáveis: {} - Código: {} - Contexto: {}",
                getMessage(), errorCode, context);

        MDC.remove("errorCode");
        MDC.remove("exceptionType");
        if (context != null) {
            context.keySet().forEach(MDC::remove);
        }
    }
}
