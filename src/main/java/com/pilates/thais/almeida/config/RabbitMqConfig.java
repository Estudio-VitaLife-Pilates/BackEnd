package com.pilates.thais.almeida.config;

import com.pilates.thais.almeida.event.LembreteAulaEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_ESTUDIO = "estudio-exchange";

    public static final String QUEUE_LEMBRETE_AULA = "aula.lembrete";

    @Bean
    public DirectExchange estudioExchange() {
        return new DirectExchange(EXCHANGE_ESTUDIO);
    }

    @Bean
    public Queue lembreteAulaQueue() {
        return new Queue(QUEUE_LEMBRETE_AULA, true);
    }

    @Bean
    public Binding lembreteAulaBinding(Queue lembreteAulaQueue, DirectExchange estudioExchange) {
        return BindingBuilder.bind(lembreteAulaQueue)
                .to(estudioExchange)
                .with(LembreteAulaEvent.ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter(LembreteAulaEvent.class.getPackageName());
    }
}
