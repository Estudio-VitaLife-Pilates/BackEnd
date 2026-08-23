package com.pilates.thais.almeida.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Exchange
    public static final String EXCHANGE_ESTUDIO = "estudio-exchange";

    // Filas e Routing Keys
    public static final String QUEUE_AULA_AGENDADA = "aula.agendada";
    public static final String ROUTING_KEY_AULA_AGENDADA = "evento.aula.agendada";


    public static final String QUEUE_AUDIT_LOG = "audit.log";
    public static final String ROUTING_KEY_AUDIT_LOG = "evento.audit.log";

    // Exchange
    @Bean
    public TopicExchange estudioExchange() {
        return new TopicExchange(EXCHANGE_ESTUDIO, true, false);
    }

    // Filas
    @Bean
    public Queue aulaAgendadaQueue() {
        return new Queue(QUEUE_AULA_AGENDADA, true);
    }


    @Bean
    public Queue auditLogQueue() {
        return new Queue(QUEUE_AUDIT_LOG, true);
    }

    // Bindings
    @Bean
    public Binding aulaAgendadaBinding(Queue aulaAgendadaQueue, TopicExchange estudioExchange) {
        return BindingBuilder.bind(aulaAgendadaQueue)
                .to(estudioExchange)
                .with(ROUTING_KEY_AULA_AGENDADA);
    }

    @Bean
    public Binding auditLogBinding(Queue auditLogQueue, TopicExchange estudioExchange) {
        return BindingBuilder.bind(auditLogQueue)
                .to(estudioExchange)
                .with(ROUTING_KEY_AUDIT_LOG);
    }
}
