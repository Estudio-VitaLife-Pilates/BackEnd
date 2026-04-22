package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aluno.AlunoDetailsResponseDto;
import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.mapper.AlunoMapper;
import com.pilates.thais.almeida.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponseDto>> obterTodos(){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterTodos()));
    }

    @GetMapping("/details")
    public ResponseEntity<List<AlunoDetailsResponseDto>> obterTodosComPlanos(){
        return ResponseEntity.status(200).body(AlunoMapper.toResponseDetails(alunoService.obterTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> obterPorId(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterPorId(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<AlunoDetailsResponseDto> obterPorIdComPlanos(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponseDetails(alunoService.obterPorId(id)));
    }

    @GetMapping("/buscar/nome")
    public ResponseEntity<List<AlunoResponseDto>> buscarPorNome(
            @RequestParam String nome
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.buscarPorNome(nome)));
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDto> criar(
            @Valid @RequestBody AlunoRequestDto request
    ){
        return ResponseEntity.status(201).body(AlunoMapper.toResponse(alunoService.criar(AlunoMapper.toEntity(request))));
    }

    @PostMapping("/planos/{idAluno}/{idPlano}")
    public ResponseEntity<AlunoResponseDto> associarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano
    ){
        return ResponseEntity.status(201).body(AlunoMapper.toResponse(alunoService.associarPlano(idAluno, idPlano)));
    }

    @DeleteMapping("/planos/{idAluno}/{idPlano}/{idAlunoPlano}/desativar")
    public ResponseEntity<Void> desativarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano,
            @PathVariable Integer idAlunoPlano
    ){
        alunoService.desativarPlano(idAluno, idPlano, idAlunoPlano);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/planos/{idAluno}/{idPlano}/{idAlunoPlano}/reativar")
    public ResponseEntity<Void> reativarPlano(
            @PathVariable Integer idAluno,
            @PathVariable Integer idPlano,
            @PathVariable Integer idAlunoPlano
    ){
        alunoService.reativarPlano(idAluno, idPlano, idAlunoPlano);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> editar(
            @Valid @RequestBody AlunoRequestDto request,
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.editar(AlunoMapper.toEntity(request), id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAluno(
            @PathVariable Integer id
    ){
        alunoService.deletarPorId(id);
        return ResponseEntity.status(204).build();
    }
}
