package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.dashboard.DashboardAlertaResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DesempenhoMensalResponseDto;
import com.pilates.thais.almeida.dto.dashboard.MetricasAulaResponseDto;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.AlunoTurmaRepository;
import com.pilates.thais.almeida.repository.AulaAlunoRepository;
import com.pilates.thais.almeida.repository.AulaRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;

@ExtendWith(MockitoExtension.class)
class MetricasServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private AulaAlunoRepository aulaAlunoRepository;

    @Mock
    private AlunoPlanoRepository alunoPlanoRepository;

    @Mock
    private AlunoTurmaRepository alunoTurmaRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @InjectMocks
    private MetricasService service;

    @Test
    @DisplayName("Deve basear 'aulas canceladas' e 'remarcadas' na mesma origem (faltas de alunos), nao em aulas inteiras desativadas")
    void obterAlertasUsaFaltasDeAlunosComoBaseParaCanceladasERemarcadas() {
        // Given
        Mockito.when(aulaAlunoRepository.countAguardandoReagendamento()).thenReturn(2L);
        Mockito.when(aulaAlunoRepository.countFaltasNoPeriodo(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(5L);
        Mockito.when(aulaAlunoRepository.countFaltasRemarcadasNoPeriodo(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(3L);
        Mockito.when(alunoRepository.countByDataCadastroBetween(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(1L);
        Mockito.when(alunoPlanoRepository.countByAtivoFalseAndDataFimBetween(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(0L);

        // When
        DashboardAlertaResponseDto resultado = service.obterAlertas();

        // Then
        Assertions.assertEquals(5L, resultado.getAulasCanceladasMes());
        Assertions.assertEquals(3L, resultado.getAulasRemarcadasMes());

        Mockito.verify(aulaAlunoRepository).countFaltasNoPeriodo(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class));
        Mockito.verify(aulaAlunoRepository).countFaltasRemarcadasNoPeriodo(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class));
        Mockito.verify(aulaRepository, Mockito.never()).countByDataAulaBetweenAndMarcadaFalse(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Deve calcular taxa de remarcacao sobre as faltas (canceladas), nao sobre o total de aulas programadas")
    void obterMetricasAulasCalculaTaxaDeRemarcacaoSobreAsFaltas() {
        // Given
        YearMonth mes = YearMonth.of(2026, 10);
        Mockito.when(aulaAlunoRepository.countByStatusAndAula_DataAulaBetween(
                        ArgumentMatchers.eq("REALIZADA"), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(20L); // agendamentos de aluno com presenca confirmada
        Mockito.when(aulaAlunoRepository.countByAula_DataAulaBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(25L); // total de agendamentos de aluno no mes, qualquer status
        Mockito.when(aulaAlunoRepository.countFaltasNoPeriodo(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(10L); // faltas de alunos
        Mockito.when(aulaAlunoRepository.countFaltasRemarcadasNoPeriodo(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(6L); // dessas faltas, quantas ja foram remarcadas

        // When
        MetricasAulaResponseDto resultado = service.obterMetricasAulas(mes);

        // Then
        Assertions.assertEquals(20L, resultado.getAulasRealizadas());
        Assertions.assertEquals(25L, resultado.getAulasProgramadas());
        Assertions.assertEquals(10L, resultado.getAulasCanceladas()); // faltas de alunos, nao sessoes
        Assertions.assertEquals(6L, resultado.getAulasRemarcadas());
        Assertions.assertEquals(0.6, resultado.getTaxaRemarcacao(), 0.0001); // 6/10, nao 6/25

        Mockito.verify(aulaRepository, Mockito.never())
                .countByDataAulaBetweenAndMarcadaTrue(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Deve basear 'realizadas' do grafico de desempenho em AulaAluno (mesma granularidade de 'canceladas'), nao em sessoes de Aula")
    void obterDesempenhoMensalUsaAulaAlunoParaRealizadasNaMesmaGranularidadeDeCanceladas() {
        // Given
        Mockito.when(aulaAlunoRepository.countByStatusAndAula_DataAulaBetween(
                        ArgumentMatchers.eq("REALIZADA"), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(8L);
        Mockito.when(aulaAlunoRepository.countFaltasNoPeriodo(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(3L);

        // When
        java.util.List<DesempenhoMensalResponseDto> resultado = service.obterDesempenho("mensal");

        // Then
        Assertions.assertEquals(6, resultado.size());
        for (DesempenhoMensalResponseDto item : resultado) {
            Assertions.assertEquals(8L, item.getAulasRealizadas());
            Assertions.assertEquals(3L, item.getAulasCanceladas());
        }

        Mockito.verify(aulaRepository, Mockito.never())
                .countByDataAulaBetweenAndMarcadaTrue(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Deve basear 'realizadas' do grafico semanal em AulaAluno tambem")
    void obterDesempenhoSemanalUsaAulaAlunoParaRealizadas() {
        // Given
        Mockito.when(aulaAlunoRepository.countByStatusAndAula_DataAulaBetween(
                        ArgumentMatchers.eq("REALIZADA"), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(2L);
        Mockito.when(aulaAlunoRepository.countFaltasNoPeriodo(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(1L);

        // When
        java.util.List<DesempenhoMensalResponseDto> resultado = service.obterDesempenho("semanal");

        // Then
        Assertions.assertEquals(8, resultado.size());
        for (DesempenhoMensalResponseDto item : resultado) {
            Assertions.assertEquals(2L, item.getAulasRealizadas());
            Assertions.assertEquals(1L, item.getAulasCanceladas());
        }

        Mockito.verify(aulaRepository, Mockito.never())
                .countByDataAulaBetweenAndMarcadaTrue(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
