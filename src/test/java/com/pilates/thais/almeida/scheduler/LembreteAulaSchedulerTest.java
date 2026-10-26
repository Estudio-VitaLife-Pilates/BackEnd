package com.pilates.thais.almeida.scheduler;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.AulaAluno;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.event.Evento;
import com.pilates.thais.almeida.event.LembreteAulaEvent;
import com.pilates.thais.almeida.producer.EventoPublicador;
import com.pilates.thais.almeida.repository.AulaAlunoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;

import java.net.ConnectException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LembreteAulaSchedulerTest {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 8, 10, 0);

    @Mock
    private AulaAlunoRepository aulaAlunoRepository;

    @Mock
    private EventoPublicador eventoPublicador;

    private final Clock clock = Clock.fixed(AGORA.atZone(FUSO).toInstant(), FUSO);

    private LembreteAulaScheduler scheduler(long antecedenciaHoras) {
        return new LembreteAulaScheduler(aulaAlunoRepository, eventoPublicador, clock, antecedenciaHoras);
    }

    @Test
    @DisplayName("Deve publicar o lembrete e marcar como enviado quando a aula esta dentro da janela")
    void devePublicarQuandoAulaDentroDaJanela() {
        LocalDateTime inicio = AGORA.plusHours(23);
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, inicio)));

        scheduler(24).publicarLembretes();

        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(eventoPublicador).publicar(captor.capture());
        LembreteAulaEvent evento = (LembreteAulaEvent) captor.getValue();
        assertEquals(101, evento.getAulaId());
        assertEquals(1, evento.getAlunoId());
        assertEquals("Aluno 1", evento.getNomeAluno());
        assertEquals("aluno1@email.com", evento.getEmailAluno());
        assertEquals("11999990001", evento.getTelefoneAluno());
        assertEquals(inicio, evento.getDataHoraAula());
        assertEquals("Thais", evento.getNomeProfessor());
        assertEquals("QUINTA", evento.getTurmaDiaSemana());
        assertEquals(LembreteAulaEvent.ROUTING_KEY, evento.routingKey());
        verify(aulaAlunoRepository).marcarLembreteEnviado(1);
    }

    @Test
    @DisplayName("Nao deve publicar quando a aula comeca depois do fim da janela")
    void naoDevePublicarQuandoAulaAlemDaJanela() {
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, AGORA.plusHours(30))));

        scheduler(24).publicarLembretes();

        verifyNoInteractions(eventoPublicador);
        verify(aulaAlunoRepository, never()).marcarLembreteEnviado(any());
    }

    @Test
    @DisplayName("Deve usar a antecedencia configurada em vez de uma regra fixa")
    void deveRespeitarAntecedenciaConfigurada() {
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, AGORA.plusHours(30))));

        scheduler(48).publicarLembretes();

        verify(eventoPublicador).publicar(any());
        verify(aulaAlunoRepository).marcarLembreteEnviado(1);
    }

    @Test
    @DisplayName("Deve consultar o repositorio pelas datas cobertas pela antecedencia")
    void deveConsultarIntervaloDeDatasDaJanela() {
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any())).thenReturn(List.of());

        scheduler(72).publicarLembretes();

        verify(aulaAlunoRepository).buscarPendentesDeLembrete(LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 11));
    }

    @Test
    @DisplayName("Nao deve publicar aula que ja comecou")
    void naoDevePublicarAulaQueJaComecou() {
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, AGORA.minusHours(2))));

        scheduler(24).publicarLembretes();

        verifyNoInteractions(eventoPublicador);
        verify(aulaAlunoRepository, never()).marcarLembreteEnviado(any());
    }

    @Test
    @DisplayName("Deve ignorar aula cuja turma nao tem horario de inicio")
    void deveIgnorarAulaSemHorario() {
        AulaAluno semHorario = aulaAluno(1, AGORA.plusHours(5));
        semHorario.getAula().getTurma().setHoraInicio(null);
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any())).thenReturn(List.of(semHorario));

        scheduler(24).publicarLembretes();

        verifyNoInteractions(eventoPublicador);
    }

    @Test
    @DisplayName("Nao deve marcar como enviado quando a publicacao falha, e deve seguir para os proximos")
    void deveSeguirParaOsProximosQuandoUmFalha() {
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, AGORA.plusHours(2)), aulaAluno(2, AGORA.plusHours(3))));
        doThrow(new IllegalStateException("falha")).doNothing().when(eventoPublicador).publicar(any());

        scheduler(24).publicarLembretes();

        verify(aulaAlunoRepository, never()).marcarLembreteEnviado(1);
        verify(aulaAlunoRepository).marcarLembreteEnviado(2);
    }

    @Test
    @DisplayName("Deve interromper a execucao quando o RabbitMQ esta indisponivel")
    void deveInterromperQuandoBrokerIndisponivel() {
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, AGORA.plusHours(2)), aulaAluno(2, AGORA.plusHours(3))));
        doThrow(new AmqpConnectException(new ConnectException("conexao recusada")))
                .when(eventoPublicador).publicar(any());

        scheduler(24).publicarLembretes();

        verify(eventoPublicador, times(1)).publicar(any());
        verify(aulaAlunoRepository, never()).marcarLembreteEnviado(any());
    }

    @Test
    @DisplayName("Deve publicar um evento por aluno quando varios alunos tem a mesma aula")
    void devePublicarUmEventoPorAluno() {
        LocalDateTime inicio = AGORA.plusHours(4);
        when(aulaAlunoRepository.buscarPendentesDeLembrete(any(), any()))
                .thenReturn(List.of(aulaAluno(1, inicio), aulaAluno(2, inicio), aulaAluno(3, inicio)));
        doNothing().when(eventoPublicador).publicar(any());

        scheduler(24).publicarLembretes();

        verify(eventoPublicador, times(3)).publicar(any());
        verify(aulaAlunoRepository).marcarLembreteEnviado(1);
        verify(aulaAlunoRepository).marcarLembreteEnviado(2);
        verify(aulaAlunoRepository).marcarLembreteEnviado(3);
    }

    private AulaAluno aulaAluno(int id, LocalDateTime inicio) {
        Professor professor = new Professor(1, "Thais", "11988887777", true, "thais@studio.com");

        Turma turma = new Turma(Turma.DiaSemana.QUINTA, inicio.toLocalTime(), 60, 6, true);
        turma.setId(10);

        Aula aula = new Aula(100 + id, inicio.toLocalDate(), true, turma, professor, new ArrayList<>());

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setNome("Aluno " + id);
        aluno.setEmail("aluno" + id + "@email.com");
        aluno.setTelefone("1199999000" + id);
        aluno.setAtivo(true);

        return new AulaAluno(id, "AGENDADO", aula, aluno, aula);
    }
}
