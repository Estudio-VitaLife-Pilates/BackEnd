package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.professor.ProfessorReponseDto;
import com.pilates.thais.almeida.dto.professor.ProfessorRequestDto;
import com.pilates.thais.almeida.mapper.ProfessorMapper;
import com.pilates.thais.almeida.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Professores", description = "Gestão de professores")
@RequestMapping("/professores")
public class ProfessorController {
    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @Operation(summary = "Buscar professor", description = "Buscar todos os professores existentes")
    @GetMapping
    public ResponseEntity<List<ProfessorReponseDto>> listarProfessores(){
        return ResponseEntity.status(200).body(ProfessorMapper.toResponse(professorService.listar()));
    }
    @Operation(summary = "Buscar professor por id", description = "Buscar professor específico por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorReponseDto> buscarPorId(@PathVariable Integer id){
        return ResponseEntity.status(200).body(ProfessorMapper.toResponse(professorService.buscarPorId(id)));
    }

    @Operation(summary = "Cadastrar professor", description = "Cadastrar professor")
    @PostMapping
    public ResponseEntity<ProfessorReponseDto> cadastrarProfessores(@Valid @RequestBody ProfessorRequestDto requestDto){
        return ResponseEntity.status(201).body(ProfessorMapper.toResponse(professorService.cadastrar(ProfessorMapper.toEntity(requestDto))));
    }

    @Operation(summary = "Desativar professor", description = "Desativar um professor por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarProfessores(@PathVariable Integer id){
        professorService.inativar(id);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Editar professor", description = "Editar professor existente por ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorReponseDto> editarProfessores(@Valid @RequestBody ProfessorRequestDto requestDto, @PathVariable Integer id){
        return ResponseEntity.status(201).body(ProfessorMapper.toResponse(professorService.editar(ProfessorMapper.toEntity(requestDto), id)));

    }

}
