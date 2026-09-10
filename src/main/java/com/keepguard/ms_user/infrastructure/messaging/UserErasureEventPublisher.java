package com.keepguard.ms_user.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.application.dto.events.UserErasureEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserErasureEventPublisher {

    private final ObjectProvider<RabbitTemplate> rabbitTemplateProvider;
    private final ObjectMapper objectMapper;
    private final String exchange;
    private final String routingKey;

    public UserErasureEventPublisher(
            ObjectProvider<RabbitTemplate> rabbitTemplateProvider,
            ObjectMapper objectMapper,
            @Value("${keepguard.events.exchange:keepguard-events-exchange}") String exchange,
            @Value("${keepguard.events.erasure-routing-key:user.erasure.requested}") String routingKey) {
        this.rabbitTemplateProvider = rabbitTemplateProvider;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publishErasureRequested(UserErasureEventDTO event) {
        try {
            RabbitTemplate template = rabbitTemplateProvider.getIfAvailable();
            if (template == null) {
                log.warn("RabbitTemplate indisponível ao publicar user.erasure.requested para userId: {}", event.userId());
                return;
            }

            byte[] body = objectMapper.writeValueAsBytes(event);
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            props.setHeader("X-Event-Type", "user.erasure.requested");

            template.send(exchange, routingKey, new Message(body, props));
            log.info("Evento user.erasure.requested publicado com sucesso para userId: {}, exchange: {}, routingKey: {}",
                    event.userId(), exchange, routingKey);
        } catch (Exception e) {
            log.warn("Falha ao publicar evento user.erasure.requested para userId: {}: {}", event.userId(), e.getMessage());
        }
    }
}
