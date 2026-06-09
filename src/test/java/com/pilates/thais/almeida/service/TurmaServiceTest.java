package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.turma.TurmaAlunoRequest;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.AlunoTurma;
import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.entity.Turma.DiaSemana;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.exceptions.TurmaNaoEncontrada;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.AlunoTurmaRepository;
import com.pilates.thais.almeida.repository.AulaRepository;
import com.pilates.thais.almeida.repository.ProfessorRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import com.pilates.thais.almeida.strategy.CalculoVagasTurmaStrategy;
import com.pilates.thais.almeida.strategy.GeracaoDatasAulaStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AlunoTurmaRepository alunoTurmaRepository;

    @Mock
    private AlunoPlanoRepository alunoPlanoRepository;

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private AulaService aulaService;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private GeracaoDatasAulaStrategy geracaoDatasAulaStrategy;

    @Mock
    private CalculoVagasTurmaStrategy calculoVagasTurmaStrategy;

    @InjectMocks
    private TurmaService service;

    private TurmaRequestDto turmaRequest;

    @BeforeEach
    void setup() {
        turmaRequest = new TurmaRequestDto();
        turmaRequest.setDiaSemana(DiaSemana.SEGUNDA);
        turmaRequest.setHoraInicio(LocalTime.of(8, 0));
        turmaRequest.setDuracaoMinutos(60);
        turmaRequest.setCapacidadeMax(10);
        turmaRequest.setAtiva(true);
    }

    @Test
    @DisplayName("Deve listar apenas turmas ativas")
    void listarTurmasAtivas() {
        List<Turma> turmas = List.of(
                new Turma(DiaSemana.SEGUNDA, LocalTime.of(8,0), 60, 10, true)
        );
        Mockito.when(turmaRepository.findTurmasByAtivaTrue()).thenReturn(turmas);

        List<Turma> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getAtiva());
        Mockito.verify(turmaRepository).findTurmasByAtivaTrue();
    }

    @Test
    @DisplayName("Deve cadastrar uma turma")
    void cadastrarTurma() {
        Mockito.when(turmaRepository.existsByHoraInicioAndDiaSemana(
                        turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana()))
                .thenReturn(false);

        // Aceita qualquer Turma
        Mockito.when(turmaRepository.save(Mockito.any(Turma.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Turma resultado = service.cadastrar(turmaRequest);

        assertNotNull(resultado);
        assertEquals(DiaSemana.SEGUNDA, resultado.getDiaSemana());
        Mockito.verify(turmaRepository).existsByHoraInicioAndDiaSemana(turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana());
        Mockito.verify(turmaRepository).save(Mockito.any(Turma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar turma em horário ocupado")
    void cadastrarTurmaHorarioOcupado() {
        Mockito.when(turmaRepository.existsByHoraInicioAndDiaSemana(
                        turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana()))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cadastrar(turmaRequest));
        assertEquals("Ja existe uma turma nesse horário.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar turma existente")
    void atualizarTurma() {
        Turma existente = new Turma(DiaSemana.SEGUNDA, LocalTime.of(8,0), 60, 10, true);
        existente.setId(1);

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(existente));
        Mockito.when(turmaRepository.save(existente)).thenReturn(existente);

        Turma resultado = service.atualizar(turmaRequest, 1);

        assertEquals(10, resultado.getCapacidadeMax());
        assertEquals(LocalTime.of(8,0), resultado.getHoraInicio());
        Mockito.verify(turmaRepository).save(existente);
    }

    @Test
    @DisplayName("Deve lançar TurmaNaoEncontrada ao atualizar turma inexistente")
    void atualizarTurmaInexistente() {
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TurmaNaoEncontrada.class, () -> service.atualizar(turmaRequest, 1));
    }

    @Test
    @DisplayName("Deve inativar turma")
    void inativarTurma() {
        Turma turma = new Turma(DiaSemana.SEGUNDA, LocalTime.of(8,0), 60, 10, true);
        turma.setId(1);

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));

        service.inativar(1);

        assertFalse(turma.getAtiva());
        Mockito.verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Deve lançar TurmaNaoEncontrada ao inativar turma inexistente")
    void inativarTurmaInexistente() {
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TurmaNaoEncontrada.class, () -> service.inativar(1));
    }

    @Test
    @DisplayName("Deve cadastrar aluno em turma")
    void cadastrarAlunoEmTurma() {
        Turma turma = new Turma(DiaSemana.SEGUNDA, LocalTime.of(8,0), 60, 10, true);
        turma.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        TurmaAlunoRequest alunoRequest = new TurmaAlunoRequest();
        alunoRequest.setAlunoId(1);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(alunoPlanoRepository.findByAtivoIsTrueAndAluno_Id(1)).thenReturn(List.of(criarAlunoPlanoAtivo(aluno)));
        Mockito.when(alunoTurmaRepository.existsByAlunoIdAndTurmaId(1,1)).thenReturn(false);
        Mockito.when(geracaoDatasAulaStrategy.gerarDatas(Mockito.eq(turma), Mockito.any(AlunoPlano.class), Mockito.any(LocalDate.class)))
                .thenReturn(List.of());

        Turma resultado = service.cadastrarAlunoEmUmaTurma(1, alunoRequest);

        assertEquals(turma, resultado);
        Mockito.verify(alunoTurmaRepository).save(Mockito.any(AlunoTurma.class));
        Mockito.verify(geracaoDatasAulaStrategy).gerarDatas(Mockito.eq(turma), Mockito.any(AlunoPlano.class), Mockito.any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve lançar AlunoNaoEncontrado ao cadastrar aluno inexistente")
    void cadastrarAlunoNaoExistente() {
        TurmaAlunoRequest alunoRequest = new TurmaAlunoRequest();
        alunoRequest.setAlunoId(1);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AlunoNaoEncontrado.class, () -> service.cadastrarAlunoEmUmaTurma(1, alunoRequest));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException se aluno já está cadastrado")
    void cadastrarAlunoJaCadastrado() {
        Turma turma = new Turma(DiaSemana.SEGUNDA, LocalTime.of(8,0), 60, 10, true);
        turma.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        TurmaAlunoRequest alunoRequest = new TurmaAlunoRequest();
        alunoRequest.setAlunoId(1);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(alunoPlanoRepository.findByAtivoIsTrueAndAluno_Id(1)).thenReturn(List.of(criarAlunoPlanoAtivo(aluno)));
        Mockito.when(alunoTurmaRepository.existsByAlunoIdAndTurmaId(1,1)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.cadastrarAlunoEmUmaTurma(1, alunoRequest));

        assertEquals("Aluno já cadastrado nessa turma", ex.getMessage());
    }

    private AlunoPlano criarAlunoPlanoAtivo(Aluno aluno) {
        Plano plano = new Plano();
        plano.setFrequenciaSemanal(2);

        AlunoPlano alunoPlano = new AlunoPlano();
        alunoPlano.setAluno(aluno);
        alunoPlano.setPlano(plano);
        alunoPlano.setAtivo(true);
        alunoPlano.setDataFim(LocalDate.now());

        return alunoPlano;
    }
}
