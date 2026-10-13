package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.repository.ProfessorRepository;
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
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository repository;

    @InjectMocks
    private ProfessorService service;

    @Test
    @DisplayName("Deve retornar a lista dos professores corretamente")
    void deveRetornarListaDosProfessores() {

        // Given
        var listaEsperada = List.of(new Professor(1, "Thais", "(11)123456789", true, "thais@gmail.com"), new Professor(2, "Marcos", "(11)123456789", true, "marcos@gmail.com"));

        // When
        Mockito.when(repository.findAll()).thenReturn(listaEsperada);

        // Then
        List<Professor> resultado = service.listar();

        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("Thais", resultado.get(0).getNome());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver professores")
    void deveRetornarListaVazia() {

        // When
        Mockito.when(repository.findAll()).thenReturn(List.of());

        // Then
        List<Professor> resultado = service.listar();

        Assertions.assertTrue(resultado.isEmpty());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Deve cadastrar um professor corretamente")
    void deveCadastrarUmProfessor() {

        // Given
        Professor professor = new Professor(null, "Carlos", "(11)999999999", true, "carlos@gmail.com");

        Professor professorSalvo = new Professor(1, "Carlos", "(11)999999999", true, "carlos@gmail.com");

        // When
        Mockito.when(repository.save(professor)).thenReturn(professorSalvo);

        // Then
        Professor resultado = service.cadastrar(professor);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Carlos", resultado.getNome());

        Mockito.verify(repository, Mockito.times(1)).save(professor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar professor")
    void deveLancarExcecaoAoCadastrarProfessor() {

        // Given
        Professor professor = new Professor(null, "Carlos", "(11)999999999", true, "carlos@gmail.com");

        // When
        Mockito.when(repository.save(Mockito.any())).thenThrow(new RuntimeException("Erro no banco"));

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.cadastrar(professor);
        });

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve inativar um professor corretamente")
    void inativar() {

        // Given
        Professor professor = new Professor(1, "Thais", "(11)123456789", true, "thais@gmail.com");

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(professor));

        service.inativar(1);

        // Then
        Assertions.assertFalse(professor.getAtivo());

        Mockito.verify(repository, Mockito.times(1)).findById(1);

        Mockito.verify(repository, Mockito.times(1)).save(professor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inativar professor inexistente")
    void deveLancarExcecaoAoInativarProfessorInexistente() {

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.inativar(1);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(1);

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve editar um professor corretamente")
    void editar() {

        // Given
        Professor professorExistente = new Professor(1, "Thais", "(11)123456789", true, "thais@gmail.com");

        Professor professorAtualizado = new Professor(1, "Thais Almeida", "(11)987654321", true, "thais.almeida@gmail.com");

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(professorExistente));

        Mockito.when(repository.save(Mockito.any(Professor.class))).thenReturn(professorAtualizado);

        // Then
        Professor resultado = service.editar(professorAtualizado, 1);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Thais Almeida", resultado.getNome());
        Assertions.assertEquals("(11)987654321", resultado.getTelefone());
        Assertions.assertEquals("thais.almeida@gmail.com", resultado.getEmail());

        Mockito.verify(repository, Mockito.times(1)).findById(1);

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Professor.class));
    }

    @Test
    @DisplayName("Deve atualizar os dados corretamente antes de salvar")
    void deveAtualizarDadosAntesDeSalvar() {

        // Given
        Professor existente = new Professor(1, "Thais", "(11)111111111", true, "thais@gmail.com");

        Professor atualizado = new Professor(1, "Thais Almeida", "(11)999999999", true, "novo@gmail.com");

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(existente));

        Mockito.when(repository.save(Mockito.any())).thenReturn(atualizado);

        // Then
        service.editar(atualizado, 1);

        Mockito.verify(repository).save(existente);

        Assertions.assertEquals("Thais Almeida", existente.getNome());
        Assertions.assertEquals("(11)999999999", existente.getTelefone());
        Assertions.assertEquals("novo@gmail.com", existente.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao editar professor inexistente")
    void deveLancarExcecaoAoEditarProfessorInexistente() {

        // Given
        Professor professorAtualizado = new Professor(1, "Novo Nome", "(11)999999999", true, "novo@gmail.com");

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.editar(professorAtualizado, 1);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(1);

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }
}