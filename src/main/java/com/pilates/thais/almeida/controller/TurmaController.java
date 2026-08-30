package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aula.AulaDetailsResponseDto;
import com.pilates.thais.almeida.dto.turma.TurmaAlunoRequest;
import com.pilates.thais.almeida.dto.turma.TurmaDetailsResponseDto;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.mapper.AulaMapper;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.service.TurmaService;
import jakarta.persistence.criteria.CriteriaBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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

    @Operation(summary = "Buscar turmas por id", description = "Buscar turmas cadastradas por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDto>buscarPorId(@PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.buscarPorId(id)));
    }

    @Operation(summary = "Buscar turmas por professor", description = "Buscar turmas de um professor específico")
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<TurmaResponseDto>> buscarPorProfessor(@PathVariable Integer professorId){
        return ResponseEntity.status(200).body(TurmaMapper.toResponse(turmaService.listarPorProfessor(professorId)));
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
    // Buscar alunos por turma
    @Operation(summary = "Buscar alunos por turma", description = "Buscar alunos cadastrados por Id turma")
    @GetMapping("/{id}/alunos")
    public ResponseEntity<TurmaDetailsResponseDto> buscarAlunosPorTurma(@PathVariable Integer id){
        return ResponseEntity.status(200).body(TurmaMapper.toResponseDetails(turmaService.buscarAlunosPorTurma(id)));
    }
    //Cadastrar aluno em uma Turma
    @Operation(summary = "Cadastrar alunos por turma", description = "Cadastrar alunos por Id turma")
    @PostMapping("/{id}/alunos")
    public ResponseEntity<TurmaDetailsResponseDto> cadastrarAlunoEmUmaTurma(@RequestBody TurmaAlunoRequest alunoId, @PathVariable Integer id){
        return ResponseEntity.status(201).body(TurmaMapper.toResponseDetails(turmaService.cadastrarAlunoEmUmaTurma(id,alunoId)));
    }
    //excluir aluno da turma
    @Operation(summary = "Excluir alunos por turma", description = "Excluir alunos cadastrados por Id turma")
    @DeleteMapping("/{id}/alunos/{alunoId}")
    public ResponseEntity<TurmaDetailsResponseDto> inativarAlunoDaTurma(@PathVariable Integer id,@PathVariable Integer alunoId){
        return ResponseEntity.status(200).body(TurmaMapper.toResponseDetails(turmaService.excluirAlunoDaTurma(id,alunoId)));
    }
    //Verificar vagas na turma
    @Operation(summary = "Buscar vagas por turma", description = "Buscar vagas cadastrados por Id turma")
    @GetMapping("{id}/vagas")
    public ResponseEntity<Integer> vagasDisponiveis(@PathVariable Integer id){
        return ResponseEntity.status(200).body(turmaService.vagasDisponiveis(id));
    }
    //buscar por dia da semana
    @Operation(summary = "Buscar turma por dia da semana", description = "Buscar turma por dia da semana")
    @GetMapping("/buscar")
    public ResponseEntity<List<TurmaResponseDto>> buscarPorDiaDaSemana(
            @RequestParam(required = false) String diaSemana
    ) {
        return ResponseEntity.ok(  TurmaMapper.toResponse(turmaService.buscarPorDiaDaSemana(diaSemana)) );
    }

    @Operation(summary = "Trocar professor", description = "Trocar o professor dessa turma - ALTERA TB DE TODAS AS AULAS POSTERIORES AO DIA DE HOJE")
    @PutMapping("/{idTurma}/professor/{idProfessor}/trocar")
    public ResponseEntity<TurmaDetailsResponseDto> trocarProfessor(
            @PathVariable Integer idTurma,
            @PathVariable Integer idProfessor
    ) {
        TurmaDetailsResponseDto turma = TurmaMapper.toResponseDetails(turmaService.trocarProfessor(idTurma, idProfessor));
        return ResponseEntity.status(200).body(turma);
    }

}
