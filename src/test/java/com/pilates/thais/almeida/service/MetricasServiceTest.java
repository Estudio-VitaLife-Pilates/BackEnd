package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.dashboard.DashboardAlertaResponseDto;
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
}
