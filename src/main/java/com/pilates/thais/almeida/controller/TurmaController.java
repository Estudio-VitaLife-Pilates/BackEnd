package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.service.TurmaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }
    @GetMapping
    public ResponseEntity<List<TurmaResponseDto>> listarTurmas(){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.listar()));

    }
    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDto>buscarPorId(@PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.buscarPorId(id)));
    }
    @PostMapping
    public ResponseEntity<TurmaResponseDto> cadastrarTurma(@Valid @RequestBody TurmaRequestDto requestDto){
        return  ResponseEntity.status(201).body(TurmaMapper.toResponse(turmaService.cadastrar(requestDto)) );
    }
    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDto> atualizarTurma(@Valid @RequestBody TurmaRequestDto requestDto, @PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.atualizar(requestDto, id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarTurma(@PathVariable Integer id ){
        turmaService.inativar(id);
        return ResponseEntity.status(204).build();
    }

}
