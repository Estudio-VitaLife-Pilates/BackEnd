package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aluno.AlunoDetailsResponseDto;
import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.mapper.AlunoMapper;
import com.pilates.thais.almeida.service.AlunoService;
<<<<<<< feature/documentacao-swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
=======
>>>>>>> develop
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
<<<<<<< feature/documentacao-swagger
@Tag(name = "Alunos", description = "Operações relacionadas a alunos")
=======
>>>>>>> develop
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Buscar aluno", description = "Buscar todos os alunos do sistema")
=======
>>>>>>> develop
    @GetMapping
    public ResponseEntity<List<AlunoResponseDto>> obterTodos(){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterTodos()));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Buscar aluno", description = "Buscar todos os alunos do sistema com planos ATIVOS")
=======
>>>>>>> develop
    @GetMapping("/details")
    public ResponseEntity<List<AlunoDetailsResponseDto>> obterTodosComPlanos(){
        return ResponseEntity.status(200).body(AlunoMapper.toResponseDetails(alunoService.obterTodos()));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Buscar aluno", description = "Buscar aluno por ID")
=======
>>>>>>> develop
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> obterPorId(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterPorId(id)));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Buscar aluno", description = "Buscar alunos com planos ativos por ID")
=======
>>>>>>> develop
    @GetMapping("/{id}/details")
    public ResponseEntity<AlunoDetailsResponseDto> obterPorIdComPlanos(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponseDetails(alunoService.obterPorId(id)));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Buscar aluno", description = "Buscar aluno por nome")
=======
>>>>>>> develop
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<AlunoResponseDto>> buscarPorNome(
            @RequestParam String nome
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.buscarPorNome(nome)));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Criar aluno", description = "Cria um novo aluno no sistema")
=======
>>>>>>> develop
    @PostMapping
    public ResponseEntity<AlunoResponseDto> criar(
            @Valid @RequestBody AlunoRequestDto request
    ){
        return ResponseEntity.status(201).body(AlunoMapper.toResponse(alunoService.criar(AlunoMapper.toEntity(request))));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Associar plano", description = "Associar um plano a um aluno")
=======
>>>>>>> develop
    @PostMapping("/planos/{idAluno}/{idPlano}")
    public ResponseEntity<AlunoResponseDto> associarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano
    ){
        return ResponseEntity.status(201).body(AlunoMapper.toResponse(alunoService.associarPlano(idAluno, idPlano)));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Desativar plano", description = "Desativar plano de um aluno")
=======
>>>>>>> develop
    @DeleteMapping("/planos/{idAluno}/{idPlano}/{idAlunoPlano}/desativar")
    public ResponseEntity<Void> desativarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano,
            @PathVariable Integer idAlunoPlano
    ){
        alunoService.desativarPlano(idAluno, idPlano, idAlunoPlano);
        return ResponseEntity.status(204).build();
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Reativar plano", description = "Reativar um plano de um aluno")
=======
>>>>>>> develop
    @PutMapping("/planos/{idAluno}/{idPlano}/{idAlunoPlano}/reativar")
    public ResponseEntity<Void> reativarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano,
            @PathVariable Integer idAlunoPlano
    ){
        alunoService.reativarPlano(idAluno, idPlano, idAlunoPlano);
        return ResponseEntity.status(204).build();
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Buscar aluno", description = "Alterar dados do aluno")
=======
>>>>>>> develop
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> editar(
            @Valid @RequestBody AlunoRequestDto request,
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.editar(AlunoMapper.toEntity(request), id)));
    }

<<<<<<< feature/documentacao-swagger
    @Operation(summary = "Apagar aluno", description = "Apagar o aluno do sistema")
=======
>>>>>>> develop
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAluno(
            @PathVariable Integer id
    ){
        alunoService.deletarPorId(id);
        return ResponseEntity.status(204).build();
    }
}
