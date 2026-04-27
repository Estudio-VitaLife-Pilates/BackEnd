package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Turmas", description = "Gestão de turmas")
@RequestMapping("/turmas")
public class TurmaController {
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @Operation(summary = "Buscar turmas", description = "Buscar turmas cadastradas")
    @GetMapping
    public ResponseEntity<List<TurmaResponseDto>> listarTurmas(){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.listar()));

    }

    @Operation(summary = "Cadastrar turma", description = "Cadastrar uma turma")
    @PostMapping
    public ResponseEntity<TurmaResponseDto> cadastrarTurma(@Valid @RequestBody TurmaRequestDto requestDto){
        return  ResponseEntity.status(201).body(TurmaMapper.toResponse(turmaService.cadastrar(requestDto)) );
    }

    @Operation(summary = "Editar turma", description = "Editar turma existente por ID")
    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDto> atualizarTurma(@Valid @RequestBody TurmaRequestDto requestDto, @PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.atualizar(requestDto, id)));
    }

    @Operation(summary = "Desativar turma", description = "Desativar turma existente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarTurma(@PathVariable Integer id ){
        turmaService.inativar(id);
        return ResponseEntity.status(204).build();
    }

}
