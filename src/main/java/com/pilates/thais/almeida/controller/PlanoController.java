package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.plano.PlanoDetailsResponseDto;
import com.pilates.thais.almeida.dto.plano.PlanoRequestDto;
import com.pilates.thais.almeida.dto.plano.PlanoResponseDto;
import com.pilates.thais.almeida.mapper.PlanoMapper;
import com.pilates.thais.almeida.service.PlanoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Planos", description = "Gestão de planos")
@RequestMapping("/planos")
public class PlanoController {
    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @Operation(summary = "Buscar planos", description = "Buscar todos os planos cadastrados")
    @GetMapping
    public ResponseEntity<List<PlanoResponseDto>> obterTodos(){
        return ResponseEntity.status(200).body(PlanoMapper.toResponse(planoService.buscarTodos()));
    }

    @Operation(summary = "Buscar planos", description = "Buscar todos os planos os detalhes dos planos")
    @GetMapping("/details")
    public ResponseEntity<List<PlanoDetailsResponseDto>> obterTodosComAlunos(){
        return ResponseEntity.status(200).body(PlanoMapper.toResponseDetails(planoService.buscarTodos()));
    }

    @Operation(summary = "Buscar planos", description = "Buscar planos por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDto> obterPorId(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(PlanoMapper.toResponse(planoService.buscarPorId(id)));
    }

    @Operation(summary = "Buscar planos", description = "Buscar planos por ID com alunos associados")
    @GetMapping("/{id}/details")
    public ResponseEntity<PlanoDetailsResponseDto> obterPorIdComAlunos(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(PlanoMapper.toResponseDetails(planoService.buscarPorId(id)));
    }

    @Operation(summary = "Cadastrar plano", description = "Criar um plano")
    @PostMapping
    public ResponseEntity<PlanoResponseDto> criar(
            @Valid @RequestBody PlanoRequestDto plano
    ){
        return ResponseEntity.status(201).body(PlanoMapper.toResponse(planoService.criar(PlanoMapper.toEntity(plano))));
    }

    @Operation(summary = "Editar plano", description = "Editar um plano existente por ID")
    @PutMapping("/{id}")
    public ResponseEntity<PlanoResponseDto> editarPorId(
            @PathVariable Integer id,
            @Valid @RequestBody PlanoRequestDto dto
    ){
        return ResponseEntity.status(200).body(PlanoMapper.toResponse(planoService.editarPorId(PlanoMapper.toEntity(dto), id)));
    }

    @Operation(summary = "Deletar plano", description = "Deletar um plano existente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Integer id
    ){
        planoService.deletarPorId(id);
        return ResponseEntity.status(204).build();
    }
}
