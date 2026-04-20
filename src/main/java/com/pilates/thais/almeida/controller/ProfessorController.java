package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.professor.ProfessorReponseDto;
import com.pilates.thais.almeida.dto.professor.ProfessorRequestDto;
import com.pilates.thais.almeida.mapper.ProfessorMapper;
import com.pilates.thais.almeida.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professores")
public class ProfessorController {
    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public ResponseEntity<List<ProfessorReponseDto>> listarProfessores(){
        return ResponseEntity.status(200).body(ProfessorMapper.toResponse(professorService.listar()));
    }
    //Buscar turmas por professora
    @PostMapping
    public ResponseEntity<ProfessorReponseDto> cadastrarProfessores(@Valid @RequestBody ProfessorRequestDto requestDto){
        return ResponseEntity.status(201).body(ProfessorMapper.toResponse(professorService.cadastrar(ProfessorMapper.toEntity(requestDto))));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarProfessores(@PathVariable Integer id){
        professorService.inativar(id);
        return ResponseEntity.status(204).build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorReponseDto> editarProfessores(@Valid @RequestBody ProfessorRequestDto requestDto, @PathVariable Integer id){
        return ResponseEntity.status(201).body(ProfessorMapper.toResponse(professorService.editar(ProfessorMapper.toEntity(requestDto), id)));

    }

}
