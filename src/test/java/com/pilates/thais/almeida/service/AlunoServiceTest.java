package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.PlanoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AlunoPlanoRepository alunoPlanoRepository;

    @InjectMocks
    private AlunoService service;

    // -------------------- GET /alunos e GET /alunos/details --------------------

    @Test
    @DisplayName("Deve retornar lista de alunos corretamente")
    void deveRetornarListaDeAlunos() {

        // Given
        Aluno a1 = new Aluno();
        a1.setId(1);
        a1.setNome("Ana Paula");
        a1.setEmail("ana@gmail.com");
        a1.setAtivo(true);

        Aluno a2 = new Aluno();
        a2.setId(2);
        a2.setNome("Carlos Silva");
        a2.setEmail("carlos@gmail.com");
        a2.setAtivo(true);

        // When
        Mockito.when(alunoRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Aluno> resultado = service.obterTodos();

        // Then
        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("Ana Paula", resultado.get(0).getNome());
        Assertions.assertEquals("Carlos Silva", resultado.get(1).getNome());

        Mockito.verify(alunoRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver alunos")
    void deveRetornarListaVaziaQuandoNaoHouverAlunos() {

        // When
        Mockito.when(alunoRepository.findAll()).thenReturn(List.of());

        List<Aluno> resultado = service.obterTodos();

        // Then
        Assertions.assertTrue(resultado.isEmpty());

        Mockito.verify(alunoRepository, Mockito.times(1)).findAll();
    }

    // -------------------- GET /alunos/{id} e GET /alunos/{id}/details --------------------

    @Test
    @DisplayName("Deve retornar aluno por ID corretamente")
    void deveRetornarAlunoPorId() {

        // Given
        Aluno aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("Ana Paula");
        aluno.setEmail("ana@gmail.com");
        aluno.setAtivo(true);

        // When
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));

        Aluno resultado = service.obterPorId(1);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Ana Paula", resultado.getNome());

        Mockito.verify(alunoRepository, Mockito.times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno com ID inexistente")
    void deveLancarExcecaoAoBuscarAlunoComIdInexistente() {

        // When
        Mockito.when(alunoRepository.findById(99)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(AlunoNaoEncontrado.class, () -> {
            service.obterPorId(99);
        });

        Mockito.verify(alunoRepository, Mockito.times(1)).findById(99);
    }

    // -------------------- GET /alunos/buscar/nome --------------------

    @Test
    @DisplayName("Deve retornar alunos ao buscar por nome")
    void deveRetornarAlunosAoBuscarPorNome() {

        // Given
        Aluno aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("Ana Paula");
        aluno.setEmail("ana@gmail.com");

        // When
        Mockito.when(alunoRepository.findAllByNomeContainingIgnoreCase("ana")).thenReturn(List.of(aluno));

        List<Aluno> resultado = service.buscarPorNome("ana");

        // Then
        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals("Ana Paula", resultado.get(0).getNome());

        Mockito.verify(alunoRepository, Mockito.times(1)).findAllByNomeContainingIgnoreCase("ana");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum aluno corresponder ao nome buscado")
    void deveRetornarListaVaziaQuandoNomeNaoEncontrado() {

        // When
        Mockito.when(alunoRepository.findAllByNomeContainingIgnoreCase("xyz")).thenReturn(List.of());

        List<Aluno> resultado = service.buscarPorNome("xyz");

        // Then
        Assertions.assertTrue(resultado.isEmpty());

        Mockito.verify(alunoRepository, Mockito.times(1)).findAllByNomeContainingIgnoreCase("xyz");
    }
}
