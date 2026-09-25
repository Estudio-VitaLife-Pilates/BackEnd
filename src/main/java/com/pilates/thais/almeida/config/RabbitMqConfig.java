package com.pilates.thais.almeida.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Exchange
    public static final String EXCHANGE_ESTUDIO = "estudio-exchange";

    // Fila e routing key do lembrete de aula
    public static final String QUEUE_LEMBRETE_AULA = "aula.lembrete";
    public static final String ROUTING_KEY_LEMBRETE_AULA = "evento.aula.lembrete";

    @Bean
    public TopicExchange estudioExchange() {
        return new TopicExchange(EXCHANGE_ESTUDIO, true, false);
    }

    @Bean
    public Queue lembreteAulaQueue() {
        return new Queue(QUEUE_LEMBRETE_AULA, true);
    }

    @Bean
    public Binding lembreteAulaBinding(Queue lembreteAulaQueue, TopicExchange estudioExchange) {
        return BindingBuilder.bind(lembreteAulaQueue)
                .to(estudioExchange)
                .with(ROUTING_KEY_LEMBRETE_AULA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
