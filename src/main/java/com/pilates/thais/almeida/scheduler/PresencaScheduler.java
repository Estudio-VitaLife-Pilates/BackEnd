package com.pilates.thais.almeida.scheduler;

import com.pilates.thais.almeida.service.AulaService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PresencaScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresencaScheduler.class);

    private final AulaService aulaService;

    public PresencaScheduler(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostConstruct
    @Scheduled(cron = "0 5 0 * * *")
    public void sincronizarPresencas() {
        int atualizados = aulaService.sincronizarPresencas();
        if (atualizados > 0) {
            LOGGER.info("[PRESENCA] {} registro(s) de AulaAluno passaram de AGENDADO para REALIZADA", atualizados);
        }
    }
}
