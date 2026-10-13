package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.exceptions.AlunoPlanoNaoEncontrado;
import com.pilates.thais.almeida.exceptions.PlanoNaoEncontrado;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.PlanoRepository;
import com.pilates.thais.almeida.strategy.CalculoVigenciaPlanoStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Mock
    private CalculoVigenciaPlanoStrategy calculoVigenciaPlanoStrategy;

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

    // -------------------- POST /alunos --------------------

    @Test
    @DisplayName("Deve cadastrar um aluno corretamente")
    void deveCadastrarUmAluno() {

        // Given
        Aluno aluno = new Aluno();
        aluno.setNome("Ana Paula");
        aluno.setEmail("ana@gmail.com");
        aluno.setAtivo(true);

        Aluno alunoSalvo = new Aluno();
        alunoSalvo.setId(1);
        alunoSalvo.setNome("Ana Paula");
        alunoSalvo.setEmail("ana@gmail.com");
        alunoSalvo.setAtivo(true);

        // When
        Mockito.when(alunoRepository.save(aluno)).thenReturn(alunoSalvo);

        Aluno resultado = service.criar(aluno);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Ana Paula", resultado.getNome());

        Mockito.verify(alunoRepository, Mockito.times(1)).save(aluno);
    }

    // -------------------- POST /alunos/planos/{idAluno}/{idPlano} --------------------

    @Test
    @DisplayName("Deve associar plano a um aluno corretamente")
    void deveAssociarPlanoAoAluno() {

        // Given
        Aluno aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("Ana Paula");
        aluno.setAtivo(true);

        Plano plano = new Plano();
        plano.setId(1);
        plano.setNome("Plano Mensal");
        plano.setValidadeDias(30);

        // When
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(planoRepository.findById(1)).thenReturn(Optional.of(plano));
        Mockito.when(calculoVigenciaPlanoStrategy.calcularDataFim(Mockito.any(LocalDate.class), Mockito.eq(plano)))
                .thenReturn(LocalDate.now().plusDays(30));

        Aluno resultado = service.associarPlano(1, 1);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());

        Mockito.verify(alunoRepository, Mockito.times(2)).findById(1);
        Mockito.verify(planoRepository, Mockito.times(1)).findById(1);
        Mockito.verify(alunoPlanoRepository, Mockito.times(1)).save(Mockito.any(AlunoPlano.class));
        Mockito.verify(calculoVigenciaPlanoStrategy, Mockito.times(1))
                .calcularDataFim(Mockito.any(LocalDate.class), Mockito.eq(plano));
    }

    @Test
    @DisplayName("Deve lançar exceção ao associar plano a aluno inexistente")
    void deveLancarExcecaoAoAssociarPlanoAlunoInexistente() {

        // When
        Mockito.when(alunoRepository.findById(99)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(AlunoNaoEncontrado.class, () -> {
            service.associarPlano(99, 1);
        });

        Mockito.verify(alunoRepository, Mockito.times(1)).findById(99);
        Mockito.verify(planoRepository, Mockito.never()).findById(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao associar plano inexistente ao aluno")
    void deveLancarExcecaoAoAssociarPlanoInexistente() {

        // Given
        Aluno aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("Ana Paula");

        // When
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(planoRepository.findById(99)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(PlanoNaoEncontrado.class, () -> {
            service.associarPlano(1, 99);
        });

        Mockito.verify(planoRepository, Mockito.times(1)).findById(99);
        Mockito.verify(alunoPlanoRepository, Mockito.never()).save(Mockito.any());
    }

    // -------------------- DELETE /alunos/planos/{idAluno}/{idPlano}/{idAlunoPlano}/desativar --------------------

    @Test
    @DisplayName("Deve desativar plano do aluno corretamente")
    void deveDesativarPlanoDoAluno() {

        // Given
        AlunoPlano alunoPlano = new AlunoPlano();
        alunoPlano.setId(1);
        alunoPlano.setAtivo(true);

        // When
        Mockito.when(alunoPlanoRepository.findByIdAndAluno_IdAndPlano_Id(1, 1, 1)).thenReturn(Optional.of(alunoPlano));

        service.desativarPlano(1, 1, 1);

        // Then
        Assertions.assertFalse(alunoPlano.getAtivo());

        Mockito.verify(alunoPlanoRepository, Mockito.times(1)).findByIdAndAluno_IdAndPlano_Id(1, 1, 1);
        Mockito.verify(alunoPlanoRepository, Mockito.times(1)).save(alunoPlano);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar plano não encontrado")
    void deveLancarExcecaoAoDesativarPlanoNaoEncontrado() {

        // When
        Mockito.when(alunoPlanoRepository.findByIdAndAluno_IdAndPlano_Id(99, 1, 1)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(AlunoPlanoNaoEncontrado.class, () -> {
            service.desativarPlano(1, 1, 99);
        });

        Mockito.verify(alunoPlanoRepository, Mockito.never()).save(Mockito.any());
    }

    // -------------------- PUT /alunos/planos/{idAluno}/{idPlano}/{idAlunoPlano}/reativar --------------------

    @Test
    @DisplayName("Deve reativar plano do aluno corretamente")
    void deveReativarPlanoDoAluno() {

        // Given
        AlunoPlano alunoPlano = new AlunoPlano();
        alunoPlano.setId(1);
        alunoPlano.setAtivo(false);

        // When
        Mockito.when(alunoPlanoRepository.findByIdAndAluno_IdAndPlano_Id(1, 1, 1)).thenReturn(Optional.of(alunoPlano));

        service.reativarPlano(1, 1, 1);

        // Then
        Assertions.assertTrue(alunoPlano.getAtivo());

        Mockito.verify(alunoPlanoRepository, Mockito.times(1)).findByIdAndAluno_IdAndPlano_Id(1, 1, 1);
        Mockito.verify(alunoPlanoRepository, Mockito.times(1)).save(alunoPlano);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reativar plano não encontrado")
    void deveLancarExcecaoAoReativarPlanoNaoEncontrado() {

        // When
        Mockito.when(alunoPlanoRepository.findByIdAndAluno_IdAndPlano_Id(99, 1, 1)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(AlunoPlanoNaoEncontrado.class, () -> {
            service.reativarPlano(1, 1, 99);
        });

        Mockito.verify(alunoPlanoRepository, Mockito.never()).save(Mockito.any());
    }

    // -------------------- PUT /alunos/{id} --------------------

    @Test
    @DisplayName("Deve atualizar dados do aluno corretamente")
    void deveAtualizarDadosDoAluno() {

        // Given
        Aluno alunoExistente = new Aluno();
        alunoExistente.setId(1);
        alunoExistente.setNome("Ana Paula");
        alunoExistente.setEmail("ana@gmail.com");

        Aluno alunoAtualizado = new Aluno();
        alunoAtualizado.setNome("Ana Paula Silva");
        alunoAtualizado.setEmail("ana.silva@gmail.com");

        Aluno alunoSalvo = new Aluno();
        alunoSalvo.setId(1);
        alunoSalvo.setNome("Ana Paula Silva");
        alunoSalvo.setEmail("ana.silva@gmail.com");

        // When
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(alunoExistente));
        Mockito.when(alunoRepository.save(alunoAtualizado)).thenReturn(alunoSalvo);

        Aluno resultado = service.editar(alunoAtualizado, 1);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Ana Paula Silva", resultado.getNome());

        Mockito.verify(alunoRepository, Mockito.times(1)).findById(1);
        Mockito.verify(alunoRepository, Mockito.times(1)).save(alunoAtualizado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar aluno inexistente")
    void deveLancarExcecaoAoAtualizarAlunoInexistente() {

        // Given
        Aluno alunoAtualizado = new Aluno();
        alunoAtualizado.setNome("Ana Paula Silva");

        // When
        Mockito.when(alunoRepository.findById(99)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(AlunoNaoEncontrado.class, () -> {
            service.editar(alunoAtualizado, 99);
        });

        Mockito.verify(alunoRepository, Mockito.times(1)).findById(99);
        Mockito.verify(alunoRepository, Mockito.never()).save(Mockito.any());
    }

    // -------------------- DELETE /alunos/{id} --------------------

    @Test
    @DisplayName("Deve deletar aluno por ID corretamente")
    void deveDeletarAlunoPorId() {

        // When
        service.deletarPorId(1);

        // Then
        Mockito.verify(alunoRepository, Mockito.times(1)).deleteById(1);
    }
}
