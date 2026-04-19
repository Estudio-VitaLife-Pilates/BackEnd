package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.plano.PlanoRequestDto;
import com.pilates.thais.almeida.dto.plano.PlanoResponseDto;
import com.pilates.thais.almeida.mapper.PlanoMapper;
import com.pilates.thais.almeida.service.PlanoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planos")
public class PlanoController {
    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @GetMapping
    public ResponseEntity<List<PlanoResponseDto>> obterTodos(){
        return ResponseEntity.status(200).body(PlanoMapper.toResponse(planoService.buscarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDto> obterPorId(
            @PathVariable Integer id
    ){
        return ResponseEntity.status(200).body(PlanoMapper.toResponse(planoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<PlanoResponseDto> criar(
            @Valid @RequestBody PlanoRequestDto plano
    ){
        return ResponseEntity.status(201).body(PlanoMapper.toResponse(planoService.criar(PlanoMapper.toEntity(plano))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanoResponseDto> editarPorId(
            @PathVariable Integer id,
            @Valid @RequestBody PlanoRequestDto dto
    ){
        return ResponseEntity.status(200).body(PlanoMapper.toResponse(planoService.editarPorId(PlanoMapper.toEntity(dto), id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Integer id
    ){
        planoService.deletarPorId(id);
        return ResponseEntity.status(204).build();
    }
}
