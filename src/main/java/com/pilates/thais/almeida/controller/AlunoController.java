package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aluno.AlunoDetailsResponseDto;
import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.mapper.AlunoMapper;
import com.pilates.thais.almeida.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Alunos", description = "Operações relacionadas a alunos")
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Operation(summary = "Buscar aluno", description = "Buscar todos os alunos do sistema")
    @GetMapping
    public ResponseEntity<List<AlunoResponseDto>> obterTodos(){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterTodos()));
    }

    @Operation(summary = "Buscar aluno", description = "Buscar todos os alunos do sistema com planos ATIVOS")
    @GetMapping("/details")
    public ResponseEntity<List<AlunoDetailsResponseDto>> obterTodosComPlanos(){
        return ResponseEntity.status(200).body(AlunoMapper.toResponseDetails(alunoService.obterTodos()));
    }

    @Operation(summary = "Buscar aluno", description = "Buscar aluno por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> obterPorId(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterPorId(id)));
    }

    @Operation(summary = "Buscar aluno", description = "Buscar alunos com planos ativos por ID")
    @GetMapping("/{id}/details")
    public ResponseEntity<AlunoDetailsResponseDto> obterPorIdComPlanos(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponseDetails(alunoService.obterPorId(id)));
    }

    @Operation(summary = "Buscar aluno", description = "Buscar aluno por nome")
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<AlunoResponseDto>> buscarPorNome(
            @RequestParam String nome
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.buscarPorNome(nome)));
    }

    @Operation(summary = "Criar aluno", description = "Cria um novo aluno no sistema")
    @PostMapping
    public ResponseEntity<AlunoResponseDto> criar(
            @Valid @RequestBody AlunoRequestDto request
    ){
        return ResponseEntity.status(201).body(AlunoMapper.toResponse(alunoService.criar(AlunoMapper.toEntity(request))));
    }

    @Operation(summary = "Associar plano", description = "Associar um plano a um aluno")
    @PostMapping("/planos/{idAluno}/{idPlano}")
    public ResponseEntity<AlunoResponseDto> associarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano
    ){
        return ResponseEntity.status(201).body(AlunoMapper.toResponse(alunoService.associarPlano(idAluno, idPlano)));
    }

    @Operation(summary = "Desativar plano", description = "Desativar plano de um aluno")
    @DeleteMapping("/planos/{idAluno}/{idPlano}/{idAlunoPlano}/desativar")
    public ResponseEntity<Void> desativarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano,
            @PathVariable Integer idAlunoPlano
    ){
        alunoService.desativarPlano(idAluno, idPlano, idAlunoPlano);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Reativar plano", description = "Reativar um plano de um aluno")
    @PutMapping("/planos/{idAluno}/{idPlano}/{idAlunoPlano}/reativar")
    public ResponseEntity<Void> reativarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano,
            @PathVariable Integer idAlunoPlano
    ){
        alunoService.reativarPlano(idAluno, idPlano, idAlunoPlano);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Buscar aluno", description = "Alterar dados do aluno")
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> editar(
            @Valid @RequestBody AlunoRequestDto request,
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.editar(AlunoMapper.toEntity(request), id)));
    }

    @Operation(summary = "Apagar aluno", description = "Apagar o aluno do sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAluno(
            @PathVariable Integer id
    ){
        alunoService.deletarPorId(id);
        return ResponseEntity.status(204).build();
    }
}
