package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.aula.AulaAlunoDetailsResponseDto;
import com.pilates.thais.almeida.dto.aula.AulaDetailsResponseDto;
import com.pilates.thais.almeida.dto.aula.AulaRequestDto;
import com.pilates.thais.almeida.dto.aula.AulaResponseDto;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.mapper.AulaMapper;
import com.pilates.thais.almeida.service.AulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Aulas", description = "Gestão de aulas")
@RequestMapping("/aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @Operation(summary = "Buscar aulas", description = "Buscar todas as aulas")
    @GetMapping
    public ResponseEntity<List<AulaResponseDto>> obterTodas() {
        List<AulaResponseDto> aulas = AulaMapper.toResponse(aulaService.obterTodas());
        return ResponseEntity.status(200).body(aulas);
    }

    @Operation(summary = "Buscar detalhes da aula", description = "Buscar todas as aulas com detalhes")
    @GetMapping("/details")
    public ResponseEntity<List<AulaDetailsResponseDto>> obterTodasComDetalhes() {
        List<AulaDetailsResponseDto> aulas = AulaMapper.toResponseDetails(aulaService.obterTodas());
        return ResponseEntity.status(200).body(aulas);
    }

    @Operation(summary = "Buscar aula", description = "Buscar aulas por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AulaResponseDto> obterPorId(@PathVariable Integer id) {
        AulaResponseDto aula = AulaMapper.toResponse(aulaService.obterPorId(id));
        return ResponseEntity.status(200).body(aula);
    }

    @Operation(summary = "Buscar aula", description = "Buscar aula especifica por ID mostrando todos os dados")
    @GetMapping("/{id}/details")
    public ResponseEntity<AulaDetailsResponseDto> obterPorIdComDetalhes(@PathVariable Integer id) {
        AulaDetailsResponseDto aula = AulaMapper.toResponseDetails(aulaService.obterPorId(id));
        return ResponseEntity.status(200).body(aula);
    }

    @Operation(summary = "Buscar aula", description = "Buscar todas as aulas por data")
    @GetMapping("/buscar/data")
    public ResponseEntity<List<AulaResponseDto>> buscarPorData(@RequestParam LocalDate data) {
        List<AulaResponseDto> aulas = AulaMapper.toResponse(aulaService.buscarPorData(data));
        return ResponseEntity.status(200).body(aulas);
    }

    @Operation(summary = "Criar aula", description = "Criar aula")
    @PostMapping
    public ResponseEntity<AulaResponseDto> criar(@Valid @RequestBody AulaRequestDto request) {  // Corrigido o nome da classe aqui
        AulaResponseDto aulaResponse = AulaMapper.toResponse(aulaService.criarAula(request.getTurmaId(), request.getProfessorId(), AulaMapper.toEntity(request)));
        return ResponseEntity.status(201).body(aulaResponse);
    }

    @Operation(summary = "Editar aula", description = "Editar uma aula existente")
    @PutMapping("/{id}")
    public ResponseEntity<AulaResponseDto> editar(@Valid @RequestBody AulaRequestDto request, @PathVariable Integer id) {  // Corrigido o nome da classe aqui
        AulaResponseDto aulaResponse = AulaMapper.toResponse(aulaService.editarAula(id, AulaMapper.toEntity(request)));
        return ResponseEntity.status(200).body(aulaResponse);
    }

    @Operation(summary = "Excluir aula", description = "Excluir uma aula existente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Integer id) {
        aulaService.deletarAula(id);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Desativar aula", description = "Desativar uma aula existente")
    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarAula(@PathVariable Integer id) {
        aulaService.desativarAula(id);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Reativar aula", description = "Reativar uma aula que estava desativada")
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Void> reativarAula(@PathVariable Integer id) {
        aulaService.reativarAula(id);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Trocar professor", description = "Trocar o professor dessa aula em específico (NÃO TROCA DA TURMA - APENAS DESSA AULA)")
    @PutMapping("/{idAula}/professor/{idProfessor}/trocar")
    public ResponseEntity<AulaDetailsResponseDto> trocarProfessor(
            @PathVariable Integer idAula,
            @PathVariable Integer idProfessor
    ) {
        AulaDetailsResponseDto aula = AulaMapper.toResponseDetails(aulaService.trocarProfessor(idAula, idProfessor));
        return ResponseEntity.status(200).body(aula);
    }
    @Operation(summary = "Buscar alunos da proxima aula da turma", description = "Lista os alunos (matriculados e em reposicao) da proxima aula agendada de uma turma")
    @GetMapping("/turma/{turmaId}/proxima")
    public ResponseEntity<List<AulaAlunoDetailsResponseDto>> buscarAlunosProximaAulaDaTurma(@PathVariable Integer turmaId) {
        Aula proximaAula = aulaService.buscarProximaAulaDaTurma(turmaId);

        if (proximaAula == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(AulaMapper.toAlunoDetails(aulaService.listarAlunosDaAula(proximaAula.getId())));
    }

    @Operation(summary = "Remover aluno de uma aula", description = "Remove um aluno de uma aula especifica (cancela presenca ou reposicao)")
    @DeleteMapping("/alunos/{aulaAlunoId}")
    public ResponseEntity<Void> removerAlunoDaAula(@PathVariable Integer aulaAlunoId) {
        aulaService.removerAlunoDaAula(aulaAlunoId);
        return ResponseEntity.status(204).build();
    }
    @Operation(summary = "Listar aulas de um aluno", description = "Lista o historico de aulas (agendadas, canceladas, reposicoes) de um aluno")
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<AulaAlunoDetailsResponseDto>> listarAulasDoAluno(@PathVariable Integer alunoId) {
        return ResponseEntity.ok(AulaMapper.toAlunoDetails(aulaService.listarAulasDoAluno(alunoId)));
    }

    @Operation(summary = "Cancelar aula de um aluno", description = "Marca uma aula agendada de um aluno como cancelada")
    @PutMapping("/alunos/{aulaAlunoId}/cancelar")
    public ResponseEntity<Void> cancelarAulaDoAluno(@PathVariable Integer aulaAlunoId) {
        aulaService.cancelarAulaDoAluno(aulaAlunoId);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Registrar reposicao", description = "Registra que um aluno vai repor uma aula perdida em outra aula")
    @PostMapping("/{aulaId}/reposicao")
    public ResponseEntity<AulaAlunoDetailsResponseDto> registrarReposicao(
            @PathVariable Integer aulaId,
            @RequestBody AulaRequestDto.ReposicaoRequestDto request
    ) {
        var aulaAluno = aulaService.registrarReposicao(aulaId, request.getAulaOrigemId(), request.getAlunoId());
        return ResponseEntity.status(201).body(AulaMapper.toAlunoDetails(aulaAluno));
    }

    @Operation(summary = "Buscar proxima aula de uma turma", description = "Retorna id e data da proxima aula agendada de uma turma, usado para escolher o destino de uma reposicao")
    @GetMapping("/turma/{turmaId}/proxima/info")
    public ResponseEntity<AulaResponseDto> buscarProximaAulaInfo(@PathVariable Integer turmaId) {
        var aula = aulaService.buscarProximaAulaDaTurma(turmaId);
        if (aula == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(AulaMapper.toResponse(aula));
    }
}