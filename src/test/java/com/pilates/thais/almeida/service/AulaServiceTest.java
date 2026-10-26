package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.aula.AulaRequestDto;
import com.pilates.thais.almeida.dto.aula.AulaResponseDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.AulaAluno;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.exceptions.AulaNaoEncontrada;
import com.pilates.thais.almeida.exceptions.ProfessorNaoEncontrado;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.AulaAlunoRepository;
import com.pilates.thais.almeida.repository.AulaRepository;
import com.pilates.thais.almeida.repository.ProfessorRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AulaServiceTest {

    @Mock
    public AulaRepository repository;

    @Mock
    public AulaAlunoRepository aulaAlunoRepository;

    @Mock
    public AlunoRepository alunoRepository;

    @InjectMocks
    private AulaService service;

    @Mock
    public ProfessorRepository professorRepository;

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    public TurmaRepository turmaRepository;

    @InjectMocks
    private TurmaService turmaService;

    @Test
    @DisplayName("Deve retornar lista com aulas quando existem dados")
    void deveRetornarListaQuandoPossuiDadosComAula(){
        var listaEsperada = List.of(
                new Aula(),
                new Aula()
        );

        Mockito.when(repository.findAll()).thenReturn(listaEsperada);

        List<Aula> resultado = service.obterTodas();

        Assertions.assertFalse(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista quando não existe dados")
    void deveRetornarListaVaziaQuandoNaoHaDados(){

        var listaVazia = Collections.EMPTY_LIST;

        Mockito.when(repository.findAll()).thenReturn(listaVazia);

        List<Aula> resultado = service.obterTodas();

        Assertions.assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Quando informado id valido deve retornar a aula correspondente")
    void deveRetornarAAulaCorrespondenteQuandoOIdForValido(){
        Aula aula = new Aula();

        aula.setId(1);
        aula.setDataAula(LocalDate.of(2026, 5, 21));
        aula.setMarcada(Boolean.TRUE);

        Optional<Aula> optionalAula = Optional.of(aula);

        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(optionalAula);

        Aula resultado = service.obterPorId(1);
        Assertions.assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada quando o id for inválido ao tentar buscar")
    void deveLancarAulaNaoEncontradaComIdInvalido(){

        Mockito.lenient().when(repository.existsById(Mockito.anyInt())).thenReturn(false);

        Assertions.assertThrows(
                AulaNaoEncontrada.class, () -> service.obterPorId(1)
        );
    }

    @Test
    @DisplayName("Quando informado data valida deve retornar as aulas com data correspondente")
    void deveRetornarAsAulasCorrespondentesQuandoADataForValida (){
        Aula aula = new Aula();

        aula.setId(1);
        aula.setDataAula(LocalDate.of(2026, 5, 21));
        aula.setMarcada(Boolean.TRUE);

        Optional<Aula> optionalAula = Optional.of(aula);

        Mockito.lenient().when(repository.findById(Mockito.anyInt())).thenReturn(optionalAula);

        List<Aula> resultado = service.buscarPorData(LocalDate.of(2026, 5, 21));
        Assertions.assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar lista quando não há aulas na data informada")
    void deveRetornarListaVaziaQuandoNaoHaAulasNaDatainformada(){

        var listaVazia = Collections.EMPTY_LIST;

        Mockito.when(repository.findAll()).thenReturn(listaVazia);

        List<Aula> resultado = service.obterTodas();

        Assertions.assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve criar aula corretamente se a turmaId e o professorId existirem")
    void deveCriarAulaCorretamenteTurmaIdProfessorIdExistentes(){
        Integer turmaId = 1;
        Integer professorId = 1;

        Turma turma = new Turma();
        turma.setId(turmaId);

        Professor professor = new Professor();
        professor.setId(professorId);

        Aula aula = new Aula();

        Mockito.when(turmaRepository.findById(turmaId))
                .thenReturn(Optional.of(turma));

        Mockito.when(professorRepository.findById(professorId))
                .thenReturn(Optional.of(professor));

        Mockito.when(repository.save(any(Aula.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Aula resultado = service.criarAula(turmaId, professorId, aula);

        verify(turmaRepository).findById(turmaId);
        verify(professorRepository).findById(professorId);
        verify(repository).save(aula);
    }

    @Test
    @DisplayName("Deve lançar Runtime Exception quando não houver turma com o id colocado")
    void  deveRetornarRuntimeExceptionTurmaNaoEncontradaQuandoTurmaNaoExistir(){
        Integer turmaId = 1;
        Integer professorId = 1;

        Aula aula = new Aula();

        Mockito.when(turmaRepository.findById(turmaId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.criarAula(turmaId, professorId, aula)
        );

        assertEquals("Turma não encontrada", exception.getMessage());

        verify(turmaRepository).findById(turmaId);
        verify(professorRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar Runtime Exception quando não houver professor com o id colocado")
    void  deveRetornarRuntimeExceptionProfessorNaoEncontradaQuandoProfessorNaoExistir(){
        Integer turmaId = 1;
        Integer professorId = 1;

        Turma turma = new Turma();
        turma.setId(turmaId);

        Aula aula = new Aula();

        Mockito.when(turmaRepository.findById(turmaId))
                .thenReturn(Optional.of(turma));

        Mockito.when(professorRepository.findById(professorId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.criarAula(turmaId, professorId, aula)
        );

        assertEquals("Professor não encontrado", exception.getMessage());

        verify(turmaRepository).findById(turmaId);
        verify(professorRepository).findById(professorId);
        verify(repository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Deve editar corretamente a aula se o id corresponder com uma aula existente")
    void deveEditarCorretamenteSeOIdCorresponderComUmaAulaExistente() {
        Integer id = 1;

        Aula aulaExistente = new Aula();
        aulaExistente.setId(id);

        Aula aulaAtualizada = new Aula();
        aulaAtualizada.setId(999); // será substituído pelo ID da aula existente

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(aulaExistente));

        Mockito.when(repository.save(any(Aula.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Aula resultado = service.editarAula(id, aulaAtualizada);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());

        verify(repository).findById(id);
        verify(repository).save(aulaAtualizada);
    }

    @Test
    @DisplayName("Deve retornar AulaNaoEncontradaException se não existir aula com o id indicado ao editar")
    void deveLancarAulaNaoEncontradaComIdInvalidoAoEditar(){
        Integer id = 999;

        Aula aula = new Aula();

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.empty());

        AulaNaoEncontrada exception = assertThrows(
                AulaNaoEncontrada.class,
                () -> service.editarAula(id, aula)
        );

        assertEquals("Aula não encontrada", exception.getMessage());

        verify(repository).findById(id);
        verify(repository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Deve deletar aula se o id existir")
    void deveDeletarAulaSeOIdExistir(){
        Integer id = 1;

        service.deletarAula(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Não deve deletear se o id não existir")
    void naoDeveDeletarAulaSeOIdExistir(){
        Integer id = 1;

        verify(repository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Deve desativar a aula se o id inserido exisitr")
    void deveDesativarAulaComSucesso() {
        Integer id = 1;

        Aula aula = new Aula();
        aula.setId(id);
        aula.setMarcada(true);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(aula));

        service.desativarAula(id);

        assertFalse(aula.getMarcada());

        verify(repository).findById(id);
        verify(repository).save(aula);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada quando o id não exisitr")
    void deveLancarAulanaoEncontradaQuandoOIdNaoExisitr() {
        Integer id = 999;

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(AulaNaoEncontrada.class,
                () -> service.desativarAula(id));

        verify(repository).findById(id);
        verify(repository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Deve reativar a aula se o id existir")
    void deveReativarAulaQuandoIdExistir(){
        Integer id = 1;

        Aula aula = new Aula();
        aula.setId(id);
        aula.setMarcada(false);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(aula));

        service.reativarAula(id);

        assertTrue(aula.getMarcada());

        verify(repository).findById(id);
        verify(repository).save(aula);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada se o id for invalido")
    void deveLancarAulaNaoEncontradaSeOIdForInvalido(){
        Integer id = 999;

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.empty());

        AulaNaoEncontrada exception = assertThrows(
                AulaNaoEncontrada.class,
                () -> service.reativarAula(id)
        );

        assertEquals("Aula não encontrada", exception.getMessage());

        verify(repository).findById(id);
        verify(repository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Deve criar aula vinculada ao aluno quando turma e professor existirem")
    void deveCriarAulaComAlunoQuandoTurmaEProfessorExistirem() {
        Integer turmaId = 1;
        Integer professorId = 1;

        Turma turma = new Turma();
        turma.setId(turmaId);

        Professor professor = new Professor();
        professor.setId(professorId);

        Aluno aluno = new Aluno();
        aluno.setId(5);

        Mockito.when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        Mockito.when(professorRepository.findById(professorId)).thenReturn(Optional.of(professor));
        Mockito.when(repository.save(any(Aula.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aula resultado = service.criarAula(turmaId, professorId, LocalDate.of(2026, 5, 21), aluno);

        assertNotNull(resultado);
        assertEquals(turma, resultado.getTurma());
        assertEquals(professor, resultado.getProfessor());
        assertTrue(resultado.getMarcada());

        verify(repository).save(any(Aula.class));
        verify(aulaAlunoRepository).save(any(AulaAluno.class));
    }

    @Test
    @DisplayName("Não deve vincular aluno a aula se já estiver vinculado")
    void naoDeveVincularAlunoSeJaVinculado() {
        Aula aula = new Aula();
        aula.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        Mockito.when(aulaAlunoRepository.existsByAula_IdAndAluno_Id(1, 1)).thenReturn(true);

        service.vincularAlunoAAulaExistente(aula, aluno);

        verify(aulaAlunoRepository, never()).save(any(AulaAluno.class));
    }

    @Test
    @DisplayName("Deve vincular aluno a aula existente quando ainda não vinculado")
    void deveVincularAlunoQuandoAindaNaoVinculado() {
        Aula aula = new Aula();
        aula.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        Mockito.when(aulaAlunoRepository.existsByAula_IdAndAluno_Id(1, 1)).thenReturn(false);

        service.vincularAlunoAAulaExistente(aula, aluno);

        verify(aulaAlunoRepository).save(any(AulaAluno.class));
    }

    @Test
    @DisplayName("Deve trocar o professor da aula com sucesso")
    void deveTrocarProfessorDaAulaComSucesso() {
        Integer idAula = 1;
        Integer idProfessor = 2;

        Aula aula = new Aula();
        aula.setId(idAula);

        Professor professor = new Professor();
        professor.setId(idProfessor);

        Mockito.when(repository.findById(idAula)).thenReturn(Optional.of(aula));
        Mockito.when(professorRepository.findById(idProfessor)).thenReturn(Optional.of(professor));
        Mockito.when(repository.save(aula)).thenReturn(aula);

        Aula resultado = service.trocarProfessor(idAula, idProfessor);

        assertEquals(professor, resultado.getProfessor());
        verify(repository).save(aula);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada ao trocar professor de aula inexistente")
    void deveLancarAulaNaoEncontradaAoTrocarProfessorDeAulaInexistente() {
        Mockito.when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AulaNaoEncontrada.class, () -> service.trocarProfessor(1, 2));
        verify(professorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar ProfessorNaoEncontrado ao trocar para professor inexistente")
    void deveLancarProfessorNaoEncontradoAoTrocarProfessorInexistente() {
        Aula aula = new Aula();
        aula.setId(1);

        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aula));
        Mockito.when(professorRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ProfessorNaoEncontrado.class, () -> service.trocarProfessor(1, 2));
        verify(repository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Deve retornar a próxima aula da turma quando existir")
    void deveRetornarProximaAulaDaTurmaQuandoExistir() {
        Aula aula = new Aula();
        aula.setId(1);

        Mockito.when(repository.findFirstByTurma_IdAndDataAulaGreaterThanEqualOrderByDataAulaAsc(
                        Mockito.eq(1), any(LocalDate.class)))
                .thenReturn(Optional.of(aula));

        Aula resultado = service.buscarProximaAulaDaTurma(1);

        assertEquals(aula, resultado);
    }

    @Test
    @DisplayName("Deve retornar null quando não houver próxima aula da turma")
    void deveRetornarNullQuandoNaoHouverProximaAulaDaTurma() {
        Mockito.when(repository.findFirstByTurma_IdAndDataAulaGreaterThanEqualOrderByDataAulaAsc(
                        Mockito.eq(1), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        Aula resultado = service.buscarProximaAulaDaTurma(1);

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve listar as próximas aulas da turma ordenadas por data")
    void deveListarProximasAulasDaTurmaOrdenadasPorData() {
        Aula aula1 = new Aula();
        aula1.setDataAula(LocalDate.of(2026, 6, 10));

        Aula aula2 = new Aula();
        aula2.setDataAula(LocalDate.of(2026, 6, 1));

        Mockito.when(repository.findAllByDataAulaAfterAndTurma_Id(any(LocalDate.class), Mockito.eq(1)))
                .thenReturn(List.of(aula1, aula2));

        List<Aula> resultado = service.listarProximasAulasDaTurma(1);

        assertEquals(2, resultado.size());
        assertEquals(aula2.getDataAula(), resultado.get(0).getDataAula());
        assertEquals(aula1.getDataAula(), resultado.get(1).getDataAula());
    }

    @Test
    @DisplayName("Deve retornar a próxima aula já existente sem criar uma nova")
    void deveRetornarProximaAulaExistenteSemCriarNova() {
        Aula aulaExistente = new Aula();
        aulaExistente.setId(1);

        Mockito.when(repository.findFirstByTurma_IdAndDataAulaGreaterThanEqualOrderByDataAulaAsc(
                        Mockito.eq(1), any(LocalDate.class)))
                .thenReturn(Optional.of(aulaExistente));

        Aula resultado = service.buscarOuCriarProximaAulaDaTurma(1);

        assertEquals(aulaExistente, resultado);
        verify(turmaRepository, never()).findById(any());
        verify(repository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Deve criar a próxima aula da turma quando ainda não existir")
    void deveCriarProximaAulaDaTurmaQuandoNaoExistir() {
        Turma turma = new Turma(Turma.DiaSemana.SEGUNDA, java.time.LocalTime.of(8, 0), 60, 10, true);
        turma.setId(1);

        Professor professor = new Professor();
        professor.setId(1);
        turma.setProfessor(professor);

        Mockito.when(repository.findFirstByTurma_IdAndDataAulaGreaterThanEqualOrderByDataAulaAsc(
                        Mockito.eq(1), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turma));
        Mockito.when(repository.save(any(Aula.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aula resultado = service.buscarOuCriarProximaAulaDaTurma(1);

        assertNotNull(resultado);
        assertEquals(turma, resultado.getTurma());
        assertEquals(professor, resultado.getProfessor());
        assertEquals(java.time.DayOfWeek.MONDAY, resultado.getDataAula().getDayOfWeek());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar próxima aula de turma inexistente")
    void deveLancarExcecaoAoCriarProximaAulaDeTurmaInexistente() {
        Mockito.when(repository.findFirstByTurma_IdAndDataAulaGreaterThanEqualOrderByDataAulaAsc(
                        Mockito.eq(1), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarOuCriarProximaAulaDaTurma(1));
    }

    @Test
    @DisplayName("Deve listar os alunos de uma aula")
    void deveListarAlunosDaAula() {
        AulaAluno aulaAluno = new AulaAluno();

        Mockito.when(aulaAlunoRepository.findByAula_Id(1)).thenReturn(List.of(aulaAluno));

        List<AulaAluno> resultado = service.listarAlunosDaAula(1);

        assertEquals(1, resultado.size());
        verify(aulaAlunoRepository).findByAula_Id(1);
    }

    @Test
    @DisplayName("Deve remover o aluno da aula")
    void deveRemoverAlunoDaAula() {
        service.removerAlunoDaAula(1);

        verify(aulaAlunoRepository).deleteById(1);
    }

    @Test
    @DisplayName("Deve listar as aulas do aluno ordenadas por data")
    void deveListarAulasDoAluno() {
        AulaAluno aulaAluno = new AulaAluno();

        Mockito.when(aulaAlunoRepository.findByAluno_IdOrderByAula_DataAulaAsc(1)).thenReturn(List.of(aulaAluno));

        List<AulaAluno> resultado = service.listarAulasDoAluno(1);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve calcular as vagas disponíveis na aula")
    void deveCalcularVagasDisponiveisNaAula() {
        Turma turma = new Turma();
        turma.setCapacidadeMax(10);

        Aula aula = new Aula();
        aula.setId(1);
        aula.setTurma(turma);

        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aula));
        Mockito.when(aulaAlunoRepository.countOcupantesAtivosDaAula(1)).thenReturn(4);

        Integer resultado = service.vagasDisponiveisNaAula(1);

        assertEquals(6, resultado);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada ao calcular vagas de aula inexistente")
    void deveLancarAulaNaoEncontradaAoCalcularVagasDeAulaInexistente() {
        Mockito.when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AulaNaoEncontrada.class, () -> service.vagasDisponiveisNaAula(1));
    }

    @Test
    @DisplayName("Deve cancelar a aula do aluno")
    void deveCancelarAulaDoAluno() {
        AulaAluno aulaAluno = new AulaAluno();
        aulaAluno.setId(1);
        aulaAluno.setStatus("AGENDADO");

        Mockito.when(aulaAlunoRepository.findById(1)).thenReturn(Optional.of(aulaAluno));

        service.cancelarAulaDoAluno(1);

        assertEquals("CANCELADA", aulaAluno.getStatus());
        verify(aulaAlunoRepository).save(aulaAluno);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada ao cancelar registro de aula inexistente")
    void deveLancarAulaNaoEncontradaAoCancelarRegistroInexistente() {
        Mockito.when(aulaAlunoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AulaNaoEncontrada.class, () -> service.cancelarAulaDoAluno(1));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar reposição na mesma aula de origem")
    void deveLancarExcecaoAoRegistrarReposicaoNaMesmaAula() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.registrarReposicao(1, 1, 1));

        assertEquals("A aula de destino não pode ser a mesma que a aula de origem.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar reposição sem vagas na aula de destino")
    void deveLancarExcecaoAoRegistrarReposicaoSemVagas() {
        Turma turma = new Turma();
        turma.setCapacidadeMax(1);

        Aula aulaDestino = new Aula();
        aulaDestino.setId(2);
        aulaDestino.setTurma(turma);

        Aula aulaOrigem = new Aula();
        aulaOrigem.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(aulaDestino));
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aulaOrigem));
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(aulaAlunoRepository.countOcupantesAtivosDaAula(2)).thenReturn(1);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.registrarReposicao(2, 1, 1));

        assertEquals("Turma de destino está sem vagas para essa data.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar reposição se aluno já estiver confirmado na aula destino")
    void deveLancarExcecaoAoRegistrarReposicaoSeJaConfirmado() {
        Turma turma = new Turma();
        turma.setCapacidadeMax(10);

        Aula aulaDestino = new Aula();
        aulaDestino.setId(2);
        aulaDestino.setTurma(turma);

        Aula aulaOrigem = new Aula();
        aulaOrigem.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        AulaAluno jaConfirmado = new AulaAluno();
        jaConfirmado.setStatus("AGENDADO");

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(aulaDestino));
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aulaOrigem));
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(aulaAlunoRepository.countOcupantesAtivosDaAula(2)).thenReturn(0);
        Mockito.when(aulaAlunoRepository.findByAula_IdAndAluno_Id(2, 1)).thenReturn(Optional.of(jaConfirmado));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.registrarReposicao(2, 1, 1));

        assertEquals("Aluno já está confirmado nessa aula.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve registrar a reposição com sucesso")
    void deveRegistrarReposicaoComSucesso() {
        Turma turma = new Turma();
        turma.setCapacidadeMax(10);

        Aula aulaDestino = new Aula();
        aulaDestino.setId(2);
        aulaDestino.setTurma(turma);

        Aula aulaOrigem = new Aula();
        aulaOrigem.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        AulaAluno aulaAlunoOrigem = new AulaAluno();
        aulaAlunoOrigem.setStatus("AGENDADO");

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(aulaDestino));
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aulaOrigem));
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(aulaAlunoRepository.countOcupantesAtivosDaAula(2)).thenReturn(0);
        Mockito.when(aulaAlunoRepository.findByAula_IdAndAluno_Id(2, 1)).thenReturn(Optional.empty());
        Mockito.when(aulaAlunoRepository.findByAula_IdAndAluno_Id(1, 1)).thenReturn(Optional.of(aulaAlunoOrigem));
        Mockito.when(aulaAlunoRepository.save(any(AulaAluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AulaAluno resultado = service.registrarReposicao(2, 1, 1);

        assertEquals("REPOSICAO", resultado.getStatus());
        assertEquals(aulaDestino, resultado.getAula());
        assertEquals(aulaOrigem, resultado.getAulaOrigem());
        assertEquals("CANCELADA", aulaAlunoOrigem.getStatus());
        verify(aulaAlunoRepository, Mockito.times(2)).save(any(AulaAluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se a aula de origem não pertencer ao aluno")
    void deveLancarExcecaoSeAulaOrigemNaoPertencerAoAluno() {
        Turma turma = new Turma();
        turma.setCapacidadeMax(10);

        Aula aulaDestino = new Aula();
        aulaDestino.setId(2);
        aulaDestino.setTurma(turma);

        Aula aulaOrigem = new Aula();
        aulaOrigem.setId(1);

        Aluno aluno = new Aluno();
        aluno.setId(1);

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(aulaDestino));
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aulaOrigem));
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(aulaAlunoRepository.countOcupantesAtivosDaAula(2)).thenReturn(0);
        Mockito.when(aulaAlunoRepository.findByAula_IdAndAluno_Id(2, 1)).thenReturn(Optional.empty());
        Mockito.when(aulaAlunoRepository.findByAula_IdAndAluno_Id(1, 1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.registrarReposicao(2, 1, 1));

        assertEquals("Essa aula de origem não pertence ao aluno informado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve listar os ids dos alunos aguardando reagendamento")
    void deveListarAlunosAguardandoReagendamento() {
        Mockito.when(aulaAlunoRepository.findAlunoIdsAguardandoReagendamento()).thenReturn(List.of(1, 3));

        List<Integer> resultado = service.listarAlunosAguardandoReagendamento();

        assertEquals(List.of(1, 3), resultado);
    }

    @Test
    @DisplayName("Deve marcar falta do aluno numa aula")
    void deveMarcarFaltaDoAluno() {
        AulaAluno aulaAluno = new AulaAluno();
        aulaAluno.setId(1);
        aulaAluno.setStatus("AGENDADO");

        Mockito.when(aulaAlunoRepository.findById(1)).thenReturn(Optional.of(aulaAluno));

        service.marcarFaltaDoAluno(1);

        assertEquals("AUSENTE", aulaAluno.getStatus());
        verify(aulaAlunoRepository).save(aulaAluno);
    }

    @Test
    @DisplayName("Deve lançar AulaNaoEncontrada ao marcar falta de registro inexistente")
    void deveLancarAulaNaoEncontradaAoMarcarFaltaDeRegistroInexistente() {
        Mockito.when(aulaAlunoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AulaNaoEncontrada.class, () -> service.marcarFaltaDoAluno(1));
        verify(aulaAlunoRepository, never()).save(any(AulaAluno.class));
    }

    @Test
    @DisplayName("Deve lançar AlunoNaoEncontrado ao registrar reposição de aluno inexistente")
    void deveLancarAlunoNaoEncontradoAoRegistrarReposicao() {
        Aula aulaDestino = new Aula();
        aulaDestino.setId(2);

        Aula aulaOrigem = new Aula();
        aulaOrigem.setId(1);

        Mockito.when(repository.findById(2)).thenReturn(Optional.of(aulaDestino));
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(aulaOrigem));
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AlunoNaoEncontrado.class, () -> service.registrarReposicao(2, 1, 1));
    }
}