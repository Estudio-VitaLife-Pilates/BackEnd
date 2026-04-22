package com.pilates.thais.almeida.controller;

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

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> obterPorId(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(AlunoMapper.toResponse(alunoService.obterPorId(id)));
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
