package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaAlunoRequest;
import com.pilates.thais.almeida.dto.turma.TurmaDetailsResponseDto;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.service.TurmaService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    // Buscar alunos por turma
    @GetMapping("/{id}/alunos")
    public ResponseEntity<TurmaDetailsResponseDto> buscarAlunosPorTurma(@PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponseDetails(turmaService.buscarAlunosPorTurma(id)));
    }
    //Cadastrar aluno em uma Turma
    @PostMapping("/{id}/alunos")
    public ResponseEntity<TurmaDetailsResponseDto> cadastrarAlunoEmUmaTurma(@RequestBody TurmaAlunoRequest alunoId, @PathVariable Integer id){
        return ResponseEntity.status(201).body(TurmaMapper.toResponseDetails(turmaService.cadastrarAlunoEmUmaTurma(id,alunoId)));
    }
    //excluir aluno da turma
    @DeleteMapping("/{id}/alunos/{alunoId}")
    public ResponseEntity<TurmaDetailsResponseDto> inativarAlunoDaTurma(@PathVariable Integer id,@PathVariable Integer alunoId){
        return ResponseEntity.status(200).body(TurmaMapper.toResponseDetails(turmaService.excluirAlunoDaTurma(id,alunoId)));
    }
    //Verificar vagas na turma
    @GetMapping("{id}/vagas")
    public ResponseEntity<Integer> vagasDisponiveis(@PathVariable Integer id){
        return ResponseEntity.status(200).body(turmaService.vagasDisponiveis(id));
    }



}
