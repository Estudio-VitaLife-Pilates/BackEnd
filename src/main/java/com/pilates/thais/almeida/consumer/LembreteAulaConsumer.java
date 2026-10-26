package com.pilates.thais.almeida.consumer;

import com.pilates.thais.almeida.config.RabbitMqConfig;
import com.pilates.thais.almeida.event.LembreteAulaEvent;
import com.pilates.thais.almeida.notificacao.CanalNotificacao;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "lembrete.habilitado", havingValue = "true", matchIfMissing = true)
public class LembreteAulaConsumer implements EventoConsumidor<LembreteAulaEvent> {

    private final CanalNotificacao canalNotificacao;

    public LembreteAulaConsumer(CanalNotificacao canalNotificacao) {
        this.canalNotificacao = canalNotificacao;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_LEMBRETE_AULA)
    public void receber(LembreteAulaEvent evento) {
        processar(evento);
    }

    @Override
    public void processar(LembreteAulaEvent evento) {
        canalNotificacao.enviarLembrete(evento);
    }
}
