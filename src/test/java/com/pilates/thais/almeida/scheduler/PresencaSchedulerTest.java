package com.pilates.thais.almeida.scheduler;

import com.pilates.thais.almeida.service.AulaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PresencaSchedulerTest {

    @Mock
    private AulaService aulaService;

    @InjectMocks
    private PresencaScheduler scheduler;

    @Test
    @DisplayName("Deve chamar a sincronizacao de presencas do AulaService")
    void deveChamarSincronizacaoDePresencas() {
        Mockito.when(aulaService.sincronizarPresencas()).thenReturn(2);

        scheduler.sincronizarPresencas();

        Mockito.verify(aulaService).sincronizarPresencas();
    }
}
