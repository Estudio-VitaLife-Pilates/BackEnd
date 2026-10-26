package com.pilates.thais.almeida.consumer;

import com.pilates.thais.almeida.event.LembreteAulaEvent;
import com.pilates.thais.almeida.notificacao.CanalNotificacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LembreteAulaConsumerTest {

    @Mock
    private CanalNotificacao canalNotificacao;

    @InjectMocks
    private LembreteAulaConsumer consumer;

    @Test
    @DisplayName("Deve repassar o evento recebido da fila para o canal de notificacao")
    void deveRepassarEventoParaOCanal() {
        LembreteAulaEvent evento = new LembreteAulaEvent();

        consumer.receber(evento);

        verify(canalNotificacao).enviarLembrete(evento);
    }
}
