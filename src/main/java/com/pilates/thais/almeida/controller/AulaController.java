package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aula.AulaDetailsResponseDto;
import com.pilates.thais.almeida.dto.aula.AulaRequestDto;
import com.pilates.thais.almeida.dto.aula.AulaResponseDto;
import com.pilates.thais.almeida.mapper.AulaMapper;
import com.pilates.thais.almeida.service.AulaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @GetMapping
    public ResponseEntity<List<AulaResponseDto>> obterTodas() {
        List<AulaResponseDto> aulas = AulaMapper.toResponse(aulaService.obterTodas());
        return ResponseEntity.status(200).body(aulas);
    }

    @GetMapping("/details")
    public ResponseEntity<List<AulaDetailsResponseDto>> obterTodasComDetalhes() {
        List<AulaDetailsResponseDto> aulas = AulaMapper.toResponseDetails(aulaService.obterTodas());
        return ResponseEntity.status(200).body(aulas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaResponseDto> obterPorId(@PathVariable Integer id) {
        AulaResponseDto aula = AulaMapper.toResponse(aulaService.obterPorId(id));
        return ResponseEntity.status(200).body(aula);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<AulaDetailsResponseDto> obterPorIdComDetalhes(@PathVariable Integer id) {
        AulaDetailsResponseDto aula = AulaMapper.toResponseDetails(aulaService.obterPorId(id));
        return ResponseEntity.status(200).body(aula);
    }

    @GetMapping("/buscar/data")
    public ResponseEntity<List<AulaResponseDto>> buscarPorData(@RequestParam LocalDate data) {
        List<AulaResponseDto> aulas = AulaMapper.toResponse(aulaService.buscarPorData(data));
        return ResponseEntity.status(200).body(aulas);
    }

    @PostMapping
    public ResponseEntity<AulaResponseDto> criar(@Valid @RequestBody AulaRequestDto request) {  // Corrigido o nome da classe aqui
        AulaResponseDto aulaResponse = AulaMapper.toResponse(aulaService.criarAula(request.getTurmaId(), request.getProfessorId(), AulaMapper.toEntity(request)));
        return ResponseEntity.status(201).body(aulaResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AulaResponseDto> editar(@Valid @RequestBody AulaRequestDto request, @PathVariable Integer id) {  // Corrigido o nome da classe aqui
        AulaResponseDto aulaResponse = AulaMapper.toResponse(aulaService.editarAula(id, AulaMapper.toEntity(request)));
        return ResponseEntity.status(200).body(aulaResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Integer id) {
        aulaService.deletarAula(id);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarAula(@PathVariable Integer id) {
        aulaService.desativarAula(id);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativarAula(@PathVariable Integer id) {
        aulaService.reativarAula(id);
        return ResponseEntity.status(204).build();
    }
}