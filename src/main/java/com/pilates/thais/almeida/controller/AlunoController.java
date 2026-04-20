package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.mapper.AlunoMapper;
import com.pilates.thais.almeida.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
