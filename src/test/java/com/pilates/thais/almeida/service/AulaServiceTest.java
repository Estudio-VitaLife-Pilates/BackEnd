package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.aula.AulaRequestDto;
import com.pilates.thais.almeida.dto.aula.AulaResponseDto;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.exceptions.AulaNaoEncontrada;
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
}