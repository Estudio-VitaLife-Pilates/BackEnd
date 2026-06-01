package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.exceptions.PlanoNaoEncontrado;
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
class PlanoServiceTest {

    @Mock
    private PlanoRepository repository;

    @InjectMocks
    private PlanoService service;

    // -------------------- GET /planos e GET /planos/details --------------------

    @Test
    @DisplayName("Deve retornar lista de planos corretamente")
    void deveRetornarListaDePlanos() {

        // Given
        Plano p1 = new Plano();
        p1.setId(1);
        p1.setNome("Plano Básico");
        p1.setFrequenciaSemanal(2);
        p1.setValidadeDias(30);
        p1.setValorMensal(150.0);

        Plano p2 = new Plano();
        p2.setId(2);
        p2.setNome("Plano Premium");
        p2.setFrequenciaSemanal(5);
        p2.setValidadeDias(30);
        p2.setValorMensal(300.0);

        // When
        Mockito.when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Plano> resultado = service.buscarTodos();

        // Then
        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("Plano Básico", resultado.get(0).getNome());
        Assertions.assertEquals("Plano Premium", resultado.get(1).getNome());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver planos")
    void deveRetornarListaVaziaQuandoNaoHouverPlanos() {

        // When
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<Plano> resultado = service.buscarTodos();

        // Then
        Assertions.assertTrue(resultado.isEmpty());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    // -------------------- GET /planos/{id} e GET /planos/{id}/details --------------------

    @Test
    @DisplayName("Deve retornar plano por ID corretamente")
    void deveRetornarPlanoPorId() {

        // Given
        Plano plano = new Plano();
        plano.setId(1);
        plano.setNome("Plano Básico");
        plano.setFrequenciaSemanal(2);
        plano.setValidadeDias(30);
        plano.setValorMensal(150.0);

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(plano));

        Plano resultado = service.buscarPorId(1);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Plano Básico", resultado.getNome());

        Mockito.verify(repository, Mockito.times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar plano com ID inexistente")
    void deveLancarExcecaoAoBuscarPlanoComIdInexistente() {

        // When
        Mockito.when(repository.findById(99)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(PlanoNaoEncontrado.class, () -> {
            service.buscarPorId(99);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(99);
    }

    // -------------------- POST /planos --------------------

    @Test
    @DisplayName("Deve cadastrar plano com sucesso")
    void deveCadastrarPlanoComSucesso() {

        // Given
        Plano plano = new Plano();
        plano.setNome("Plano Básico");
        plano.setFrequenciaSemanal(2);
        plano.setValidadeDias(30);
        plano.setValorMensal(150.0);

        Plano planoSalvo = new Plano();
        planoSalvo.setId(1);
        planoSalvo.setNome("Plano Básico");
        planoSalvo.setFrequenciaSemanal(2);
        planoSalvo.setValidadeDias(30);
        planoSalvo.setValorMensal(150.0);

        // When
        Mockito.when(repository.save(plano)).thenReturn(planoSalvo);

        Plano resultado = service.criar(plano);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Plano Básico", resultado.getNome());

        Mockito.verify(repository, Mockito.times(1)).save(plano);
    }

    // -------------------- PUT /planos/{id} --------------------

    @Test
    @DisplayName("Deve atualizar plano existente corretamente")
    void deveAtualizarPlanoExistente() {

        // Given
        Plano planoExistente = new Plano();
        planoExistente.setId(1);
        planoExistente.setNome("Plano Básico");
        planoExistente.setFrequenciaSemanal(2);
        planoExistente.setValidadeDias(30);
        planoExistente.setValorMensal(150.0);

        Plano planoAtualizado = new Plano();
        planoAtualizado.setNome("Plano Intermediário");
        planoAtualizado.setFrequenciaSemanal(3);
        planoAtualizado.setValidadeDias(30);
        planoAtualizado.setValorMensal(200.0);

        Plano planoSalvo = new Plano();
        planoSalvo.setId(1);
        planoSalvo.setNome("Plano Intermediário");
        planoSalvo.setFrequenciaSemanal(3);
        planoSalvo.setValidadeDias(30);
        planoSalvo.setValorMensal(200.0);

        // When
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(planoExistente));
        Mockito.when(repository.save(planoAtualizado)).thenReturn(planoSalvo);

        Plano resultado = service.editarPorId(planoAtualizado, 1);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Plano Intermediário", resultado.getNome());
        Assertions.assertEquals(200.0, resultado.getValorMensal());

        Mockito.verify(repository, Mockito.times(1)).findById(1);
        Mockito.verify(repository, Mockito.times(1)).save(planoAtualizado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar plano inexistente")
    void deveLancarExcecaoAoAtualizarPlanoInexistente() {

        // Given
        Plano planoAtualizado = new Plano();
        planoAtualizado.setNome("Plano Novo");

        // When
        Mockito.when(repository.findById(99)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(PlanoNaoEncontrado.class, () -> {
            service.editarPorId(planoAtualizado, 99);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(99);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    // -------------------- DELETE /planos/{id} --------------------

    @Test
    @DisplayName("Deve remover plano por ID com sucesso")
    void deveRemoverPlanoPorId() {

        // When
        Mockito.when(repository.existsById(1)).thenReturn(true);

        service.deletarPorId(1);

        // Then
        Mockito.verify(repository, Mockito.times(1)).existsById(1);
        Mockito.verify(repository, Mockito.times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover plano inexistente")
    void deveLancarExcecaoAoRemoverPlanoInexistente() {

        // When
        Mockito.when(repository.existsById(99)).thenReturn(false);

        // Then
        Assertions.assertThrows(PlanoNaoEncontrado.class, () -> {
            service.deletarPorId(99);
        });

        Mockito.verify(repository, Mockito.times(1)).existsById(99);
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.any());
    }
}
