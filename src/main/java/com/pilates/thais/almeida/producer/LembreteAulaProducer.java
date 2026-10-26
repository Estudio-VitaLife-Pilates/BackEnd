package com.pilates.thais.almeida.producer;

import com.pilates.thais.almeida.config.RabbitMqConfig;
import com.pilates.thais.almeida.event.Evento;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class LembreteAulaProducer implements EventoPublicador {

    private final RabbitTemplate rabbitTemplate;

    public LembreteAulaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(Evento evento) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_ESTUDIO, evento.routingKey(), evento);
    }
}
