package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.service.TurmaService;
<<<<<<< feature/documentacao-swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
=======
>>>>>>> develop
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
<<<<<<< feature/documentacao-swagger
@Tag(name = "Turmas", description = "Gestão de turmas")
=======
>>>>>>> develop
@RequestMapping("/turmas")
public class TurmaController {
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }
<<<<<<< feature/documentacao-swagger

    @Operation(summary = "Buscar turmas", description = "Buscar turmas cadastradas")
=======
>>>>>>> develop
    @GetMapping
    public ResponseEntity<List<TurmaResponseDto>> listarTurmas(){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.listar()));

    }
<<<<<<< feature/documentacao-swagger

    @Operation(summary = "Cadastrar turma", description = "Cadastrar uma turma")
=======
>>>>>>> develop
    @PostMapping
    public ResponseEntity<TurmaResponseDto> cadastrarTurma(@Valid @RequestBody TurmaRequestDto requestDto){
        return  ResponseEntity.status(201).body(TurmaMapper.toResponse(turmaService.cadastrar(requestDto)) );
    }
<<<<<<< feature/documentacao-swagger

    @Operation(summary = "Editar turma", description = "Editar turma existente por ID")
=======
>>>>>>> develop
    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDto> atualizarTurma(@Valid @RequestBody TurmaRequestDto requestDto, @PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.atualizar(requestDto, id)));
    }
<<<<<<< feature/documentacao-swagger

    @Operation(summary = "Desativar turma", description = "Desativar turma existente")
=======
>>>>>>> develop
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarTurma(@PathVariable Integer id ){
        turmaService.inativar(id);
        return ResponseEntity.status(204).build();
    }

}
