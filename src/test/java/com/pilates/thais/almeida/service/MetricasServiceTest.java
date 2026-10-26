package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.dashboard.DashboardAlertaResponseDto;
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
        Mockito.when(aulaRepository.countByDataAulaBetweenAndMarcadaTrue(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(20L); // aulas (sessoes) realizadas
        Mockito.when(aulaRepository.countByDataAulaBetweenAndMarcadaFalse(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(5L); // sessoes inteiras desativadas
        Mockito.when(aulaAlunoRepository.countFaltasNoPeriodo(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(10L); // faltas de alunos
        Mockito.when(aulaAlunoRepository.countFaltasRemarcadasNoPeriodo(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(6L); // dessas faltas, quantas ja foram remarcadas

        // When
        MetricasAulaResponseDto resultado = service.obterMetricasAulas(mes);

        // Then
        Assertions.assertEquals(20L, resultado.getAulasRealizadas());
        Assertions.assertEquals(25L, resultado.getAulasProgramadas()); // 20 realizadas + 5 desativadas (sessao)
        Assertions.assertEquals(10L, resultado.getAulasCanceladas()); // faltas de alunos, nao sessoes
        Assertions.assertEquals(6L, resultado.getAulasRemarcadas());
        Assertions.assertEquals(0.6, resultado.getTaxaRemarcacao(), 0.0001); // 6/10, nao 6/25
    }
}
