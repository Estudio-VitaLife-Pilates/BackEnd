package com.pilates.thais.almeida.scheduler;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.AulaAluno;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.event.LembreteAulaEvent;
import com.pilates.thais.almeida.producer.EventoPublicador;
import com.pilates.thais.almeida.repository.AulaAlunoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "lembrete.habilitado", havingValue = "true", matchIfMissing = true)
public class LembreteAulaScheduler {

    private static final Logger log = LoggerFactory.getLogger(LembreteAulaScheduler.class);

    private final AulaAlunoRepository aulaAlunoRepository;
    private final EventoPublicador eventoPublicador;
    private final Clock clock;
    private final Duration antecedencia;

    public LembreteAulaScheduler(AulaAlunoRepository aulaAlunoRepository,
                                 EventoPublicador eventoPublicador,
                                 Clock clock,
                                 @Value("${lembrete.antecedencia-horas}") long antecedenciaHoras) {
        this.aulaAlunoRepository = aulaAlunoRepository;
        this.eventoPublicador = eventoPublicador;
        this.clock = clock;
        this.antecedencia = Duration.ofHours(antecedenciaHoras);
    }

    @Scheduled(fixedDelayString = "${lembrete.intervalo-minutos}", timeUnit = TimeUnit.MINUTES)
    public void publicarLembretes() {
        LocalDateTime agora = LocalDateTime.now(clock);
        LocalDateTime limite = agora.plus(antecedencia);

        List<AulaAluno> pendentes =
                aulaAlunoRepository.buscarPendentesDeLembrete(agora.toLocalDate(), limite.toLocalDate());

        int publicados = 0;
        for (AulaAluno aulaAluno : pendentes) {
            LocalDateTime inicio = inicioDaAula(aulaAluno.getAula());
            if (inicio == null || !inicio.isAfter(agora) || inicio.isAfter(limite)) {
                continue;
            }

            try {
                eventoPublicador.publicar(montarEvento(aulaAluno, inicio));
                // "enviado" = publicado na fila; o que vem depois e responsabilidade do consumer
                aulaAlunoRepository.marcarLembreteEnviado(aulaAluno.getId());
                publicados++;
            } catch (AmqpException e) {
                log.error("Falha ao falar com o RabbitMQ; execucao interrompida, os pendentes serao tentados na proxima", e);
                break;
            } catch (RuntimeException e) {
                log.error("Falha ao processar lembrete (aula {}, aluno {})",
                        aulaAluno.getAula().getId(), aulaAluno.getAluno().getId(), e);
            }
        }

        if (publicados > 0) {
            log.info("{} lembrete(s) de aula publicado(s) (antecedencia de {}h)", publicados, antecedencia.toHours());
        }
    }

    private LocalDateTime inicioDaAula(Aula aula) {
        Turma turma = aula.getTurma();
        if (aula.getDataAula() == null || turma == null || turma.getHoraInicio() == null) {
            return null;
        }
        return aula.getDataAula().atTime(turma.getHoraInicio());
    }

    private LembreteAulaEvent montarEvento(AulaAluno aulaAluno, LocalDateTime inicio) {
        Aula aula = aulaAluno.getAula();
        Aluno aluno = aulaAluno.getAluno();
        Professor professor = aula.getProfessor();
        Turma.DiaSemana diaSemana = aula.getTurma().getDiaSemana();

        return new LembreteAulaEvent(
                aula.getId(),
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(),
                inicio,
                professor != null ? professor.getNome() : null,
                diaSemana != null ? diaSemana.name() : null);
    }
}
