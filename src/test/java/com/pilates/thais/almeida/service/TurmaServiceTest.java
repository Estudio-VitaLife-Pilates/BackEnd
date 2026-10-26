package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.turma.TurmaAlunoRequest;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.AlunoTurma;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.AulaAluno;
import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.entity.Professor;
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
    private com.pilates.thais.almeida.repository.AulaAlunoRepository aulaAlunoRepository;

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

    private AlunoPlano criarAlunoPlanoAtivo() {
        Plano plano = new Plano();
        plano.setFrequenciaSemanal(2);

        AlunoPlano alunoPlano = new AlunoPlano();
        alunoPlano.setPlano(plano);
        alunoPlano.setDataInicio(LocalDate.now().minusDays(10));
        alunoPlano.setDataFim(LocalDate.now().plusDays(10)); // garante que está ativo

        return alunoPlano;
    }

    private Turma criarTurmaComProfessor() {
        Professor professor = new Professor();
        professor.setId(99);

        Turma turma = new Turma(DiaSemana.SEGUNDA, LocalTime.of(8,0), 60, 10, true);
        turma.setId(1);
        turma.setProfessor(professor);

        return turma;
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
        turmaRequest.setProfessorId(1);
        Professor professor = new Professor();
        professor.setId(1);
        Mockito.when(professorRepository.findById(1)).thenReturn(Optional.of(professor));

        Mockito.when(turmaRepository.existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrue(
                        turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana(), 1))
                .thenReturn(false);

        Mockito.when(turmaRepository.save(Mockito.any(Turma.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Turma resultado = service.cadastrar(turmaRequest);

        assertNotNull(resultado);
        assertEquals(DiaSemana.SEGUNDA, resultado.getDiaSemana());
        Mockito.verify(turmaRepository).existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrue(turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana(), 1);
        Mockito.verify(turmaRepository).save(Mockito.any(Turma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar turma em horário ocupado pelo mesmo professor")
    void cadastrarTurmaHorarioOcupado() {
        turmaRequest.setProfessorId(1);
        Professor professor = new Professor();
        professor.setId(1);
        Mockito.when(professorRepository.findById(1)).thenReturn(Optional.of(professor));
        Mockito.when(turmaRepository.existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrue(
                        turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana(), 1))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cadastrar(turmaRequest));
        assertEquals("Esse professor já tem uma turma ativa nesse horário.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar turma existente")
    void atualizarTurma() {
        turmaRequest.setProfessorId(1);
        Turma existente = criarTurmaComProfessor();
        existente.setId(1);

        Mockito.when(professorRepository.findById(1)).thenReturn(Optional.of(new Professor()));
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
    @DisplayName("Deve lançar exceção ao atualizar turma para horário já ocupado pelo mesmo professor")
    void atualizarTurmaHorarioOcupadoPeloMesmoProfessor() {
        turmaRequest.setProfessorId(1);
        Professor professor = new Professor();
        professor.setId(1);

        Turma existente = criarTurmaComProfessor();
        existente.setId(1);

        Mockito.when(professorRepository.findById(1)).thenReturn(Optional.of(professor));
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(existente));
        Mockito.when(turmaRepository.existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrueAndIdNot(
                        turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana(), 1, 1))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.atualizar(turmaRequest, 1));
        assertEquals("Esse professor já tem uma turma ativa nesse horário.", ex.getMessage());
        Mockito.verify(turmaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve permitir cadastrar turma no mesmo horário com professor diferente")
    void cadastrarTurmaMesmoHorarioProfessorDiferente() {
        turmaRequest.setProfessorId(2);
        Professor professor = new Professor();
        professor.setId(2);

        Mockito.when(professorRepository.findById(2)).thenReturn(Optional.of(professor));
        // O professor 2 não tem turma nesse horário — só o professor 1 (de outra turma) teria.
        Mockito.when(turmaRepository.existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrue(
                        turmaRequest.getHoraInicio(), turmaRequest.getDiaSemana(), 2))
                .thenReturn(false);
        Mockito.when(turmaRepository.save(Mockito.any(Turma.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Turma resultado = service.cadastrar(turmaRequest);

        assertNotNull(resultado);
        Mockito.verify(turmaRepository).save(Mockito.any(Turma.class));
    }

    @Test
    @DisplayName("Deve inativar turma")
    void inativarTurma() {
        Turma turma = criarTurmaComProfessor();

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
    @DisplayName("Deve desativar matriculas e cancelar aulas futuras ao inativar turma")
    void inativarTurmaDesativaMatriculasECancelaAulasFuturas() {
        Turma turma = criarTurmaComProfessor();

        AlunoTurma matricula = new AlunoTurma();
        matricula.setId(10);
        matricula.setAtivo(true);

        Aula aulaFutura = new Aula();
        aulaFutura.setId(20);

        AulaAluno aulaAlunoAgendado = new AulaAluno();
        aulaAlunoAgendado.setStatus("AGENDADO");

        AulaAluno aulaAlunoReposicao = new AulaAluno();
        aulaAlunoReposicao.setStatus("REPOSICAO");

        AulaAluno aulaAlunoJaCancelado = new AulaAluno();
        aulaAlunoJaCancelado.setStatus("CANCELADA");

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(alunoTurmaRepository.findAllByTurmaIdAndAtivoTrue(1)).thenReturn(List.of(matricula));
        Mockito.when(aulaRepository.findAllByDataAulaAfterAndTurma_Id(Mockito.any(LocalDate.class), Mockito.eq(1)))
                .thenReturn(List.of(aulaFutura));
        Mockito.when(aulaAlunoRepository.findByAula_Id(20))
                .thenReturn(List.of(aulaAlunoAgendado, aulaAlunoReposicao, aulaAlunoJaCancelado));

        service.inativar(1);

        assertFalse(matricula.getAtivo());
        Mockito.verify(alunoTurmaRepository).save(matricula);

        assertEquals("CANCELADA", aulaAlunoAgendado.getStatus());
        assertEquals("CANCELADA", aulaAlunoReposicao.getStatus());
        Mockito.verify(aulaAlunoRepository).save(aulaAlunoAgendado);
        Mockito.verify(aulaAlunoRepository).save(aulaAlunoReposicao);
        Mockito.verify(aulaAlunoRepository, Mockito.never()).save(aulaAlunoJaCancelado);
    }

    @Test
    @DisplayName("Deve cadastrar aluno em turma")
    void cadastrarAlunoEmTurma() {
        Turma turma = criarTurmaComProfessor();

        Aluno aluno = new Aluno();
        aluno.setId(1);

        TurmaAlunoRequest alunoRequest = new TurmaAlunoRequest();
        alunoRequest.setAlunoId(1);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(alunoPlanoRepository.findByAtivoIsTrueAndAluno_Id(1)).thenReturn(List.of(criarAlunoPlanoAtivo(aluno)));
        Mockito.when(alunoTurmaRepository.existsByAlunoIdAndTurmaIdAndAtivoTrue(1,1)).thenReturn(false);
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
        Turma turma = criarTurmaComProfessor();

        Aluno aluno = new Aluno();
        aluno.setId(1);

        TurmaAlunoRequest alunoRequest = new TurmaAlunoRequest();
        alunoRequest.setAlunoId(1);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(alunoPlanoRepository.findByAtivoIsTrueAndAluno_Id(1)).thenReturn(List.of(criarAlunoPlanoAtivo(aluno)));
        Mockito.when(alunoTurmaRepository.existsByAlunoIdAndTurmaIdAndAtivoTrue(1,1)).thenReturn(true);
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

    @Test
    @DisplayName("Deve buscar turma por id")
    void deveBuscarTurmaPorId() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));

        Turma resultado = service.buscarPorId(1);

        assertEquals(turma, resultado);
    }

    @Test
    @DisplayName("Deve lançar TurmaNaoEncontrada ao buscar turma inexistente por id")
    void deveLancarTurmaNaoEncontradaAoBuscarPorId() {
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TurmaNaoEncontrada.class, () -> service.buscarPorId(1));
    }

    @Test
    @DisplayName("Deve buscar alunos por turma")
    void deveBuscarAlunosPorTurma() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));

        Turma resultado = service.buscarAlunosPorTurma(1);

        assertEquals(turma, resultado);
    }

    @Test
    @DisplayName("Deve lançar TurmaNaoEncontrada ao buscar alunos de turma inexistente")
    void deveLancarTurmaNaoEncontradaAoBuscarAlunosPorTurma() {
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TurmaNaoEncontrada.class, () -> service.buscarAlunosPorTurma(1));
    }

    @Test
    @DisplayName("Deve excluir aluno da turma e cancelar as aulas futuras dele")
    void deveExcluirAlunoDaTurma() {
        Turma turma = criarTurmaComProfessor();

        AlunoTurma alunoTurma = new AlunoTurma();
        alunoTurma.setId(10);
        alunoTurma.setTurma(turma);

        com.pilates.thais.almeida.entity.Aula aulaFutura = new com.pilates.thais.almeida.entity.Aula();
        aulaFutura.setId(20);

        com.pilates.thais.almeida.entity.AulaAluno aulaAluno = new com.pilates.thais.almeida.entity.AulaAluno();
        aulaAluno.setStatus("AGENDADO");

        Mockito.when(alunoTurmaRepository.findByAlunoIdAndTurmaId(1, 1)).thenReturn(alunoTurma);
        Mockito.when(aulaRepository.findAllByDataAulaAfterAndTurma_Id(Mockito.any(LocalDate.class), Mockito.eq(1)))
                .thenReturn(List.of(aulaFutura));
        Mockito.when(aulaAlunoRepository.findByAula_IdAndAluno_Id(20, 1)).thenReturn(Optional.of(aulaAluno));

        Turma resultado = service.excluirAlunoDaTurma(1, 1);

        assertEquals(turma, resultado);
        assertEquals("CANCELADA", aulaAluno.getStatus());
        Mockito.verify(aulaAlunoRepository).save(aulaAluno);
        Mockito.verify(alunoTurmaRepository).removeById(10);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir aluno não matriculado na turma")
    void deveLancarExcecaoAoExcluirAlunoNaoMatriculado() {
        Mockito.when(alunoTurmaRepository.findByAlunoIdAndTurmaId(1, 1)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.excluirAlunoDaTurma(1, 1));
        assertEquals("Aluno não está matriculado nessa turma", ex.getMessage());
    }

    @Test
    @DisplayName("Deve calcular as vagas disponíveis da turma")
    void deveCalcularVagasDisponiveis() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(alunoTurmaRepository.countByTurmaIdAndAtivoTrue(1)).thenReturn(4);
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(calculoVagasTurmaStrategy.calcular(turma, 4)).thenReturn(6);

        Integer resultado = service.vagasDisponiveis(1);

        assertEquals(6, resultado);
    }

    @Test
    @DisplayName("Deve buscar turmas por dia da semana quando informado")
    void deveBuscarTurmasPorDiaDaSemanaQuandoInformado() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(turmaRepository.findByDiaSemanaIgnoreCase("SEGUNDA")).thenReturn(List.of(turma));

        List<Turma> resultado = service.buscarPorDiaDaSemana("SEGUNDA");

        assertEquals(1, resultado.size());
        Mockito.verify(turmaRepository, Mockito.never()).findAll();
    }

    @Test
    @DisplayName("Deve buscar todas as turmas quando o dia da semana não for informado")
    void deveBuscarTodasAsTurmasQuandoDiaDaSemanaNaoInformado() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(turmaRepository.findAll()).thenReturn(List.of(turma));

        List<Turma> resultado = service.buscarPorDiaDaSemana(null);

        assertEquals(1, resultado.size());
        Mockito.verify(turmaRepository, Mockito.never()).findByDiaSemanaIgnoreCase(Mockito.any());
    }

    @Test
    @DisplayName("Deve trocar o professor da turma e das aulas futuras")
    void deveTrocarProfessorDaTurma() {
        Turma turma = criarTurmaComProfessor();

        Professor novoProfessor = new Professor();
        novoProfessor.setId(2);

        com.pilates.thais.almeida.entity.Aula aulaFutura = new com.pilates.thais.almeida.entity.Aula();
        aulaFutura.setId(5);

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(professorRepository.findById(2)).thenReturn(Optional.of(novoProfessor));
        Mockito.when(aulaRepository.findAllByDataAulaAfterAndTurma_Id(Mockito.any(LocalDate.class), Mockito.eq(1)))
                .thenReturn(List.of(aulaFutura));
        Mockito.when(turmaRepository.save(turma)).thenReturn(turma);

        Turma resultado = service.trocarProfessor(1, 2);

        assertEquals(novoProfessor, resultado.getProfessor());
        assertEquals(novoProfessor, aulaFutura.getProfessor());
        Mockito.verify(aulaRepository).save(aulaFutura);
    }

    @Test
    @DisplayName("Deve lançar TurmaNaoEncontrada ao trocar professor de turma inexistente")
    void deveLancarTurmaNaoEncontradaAoTrocarProfessor() {
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TurmaNaoEncontrada.class, () -> service.trocarProfessor(1, 2));
    }

    @Test
    @DisplayName("Deve lançar ProfessorNaoEncontrado ao trocar para professor inexistente na turma")
    void deveLancarProfessorNaoEncontradoAoTrocarProfessorDaTurma() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(professorRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(com.pilates.thais.almeida.exceptions.ProfessorNaoEncontrado.class,
                () -> service.trocarProfessor(1, 2));
        Mockito.verify(turmaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve listar turmas por professor")
    void deveListarTurmasPorProfessor() {
        Turma turma = criarTurmaComProfessor();

        Mockito.when(turmaRepository.findByProfessor_Id(99)).thenReturn(List.of(turma));

        List<Turma> resultado = service.listarPorProfessor(99);

        assertEquals(1, resultado.size());
        assertEquals(turma, resultado.get(0));
    }
}
