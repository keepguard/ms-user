package com.keepguard.ms_user.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Value("${application.timezone:America/Sao_Paulo}")
    private String applicationTimeZone;

    private static final DateTimeFormatter DATETIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // Configuração customizada APENAS para LocalDateTime - formato sem milissegundos
        // OffsetDateTime usa o formato padrão do JavaTimeModule (ISO 8601 completo)
        javaTimeModule.addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(DATETIME_FORMATTER));

        return new ObjectMapper()
                // Enums: usa toString() para deserialização customizada
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                
                // Datas: formato ISO 8601 ao invés de timestamps numéricos
                // OffsetDateTime → "2025-01-06T14:29:38-03:00" (mantém timezone)
                // LocalDateTime → "2025-01-06T14:29:38" (sem timezone)
                // LocalDate → "2025-01-06" (apenas data)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                
                // Define timezone padrão para LocalDateTime (quando usado)
                .setTimeZone(TimeZone.getTimeZone(applicationTimeZone))
                
                // Suporte aos tipos de data/hora do Java 8+ (java.time.*)
                .registerModule(javaTimeModule);
    }
}
