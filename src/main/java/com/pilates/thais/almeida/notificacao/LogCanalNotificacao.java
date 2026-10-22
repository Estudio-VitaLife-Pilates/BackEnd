package com.pilates.thais.almeida.notificacao;

import com.pilates.thais.almeida.event.LembreteAulaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "lembrete.canal", havingValue = "log", matchIfMissing = true)
public class LogCanalNotificacao implements CanalNotificacao {

    private static final Logger log = LoggerFactory.getLogger(LogCanalNotificacao.class);

    @Override
    public void enviarLembrete(LembreteAulaEvent evento) {
        log.info("Lembrete de aula (canal log, nada foi enviado): aluno {} ({}), aula {} em {}, turma {}, professor {}",
                evento.getAlunoId(), evento.getNomeAluno(), evento.getAulaId(), evento.getDataHoraAula(),
                evento.getTurmaDiaSemana(), evento.getNomeProfessor());
    }
}
