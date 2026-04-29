package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.exceptions.AulaNaoEncontrada;
import com.pilates.thais.almeida.repository.AulaRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import com.pilates.thais.almeida.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public AulaService(AulaRepository aulaRepository, TurmaRepository turmaRepository, ProfessorRepository professorRepository) {
        this.aulaRepository = aulaRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    public List<Aula> obterTodas() {
        return aulaRepository.findAll();
    }

    public Aula obterPorId(Integer id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new AulaNaoEncontrada("Aula não encontrada"));
    }

    public List<Aula> buscarPorData(LocalDate data) {
        return aulaRepository.findAllByDataAula(data);
    }

    public Aula criarAula(Integer turmaId, Integer professorId, Aula aula) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        aula.setTurma(turma);
        aula.setProfessor(professor);
        aula.setDataAula(LocalDate.now());
        aula.setMarcada(true);

        return aulaRepository.save(aula);
    }

    public Aula editarAula(Integer id, Aula aula) {
        Optional<Aula> aulaExistente = aulaRepository.findById(id);

        if (aulaExistente.isPresent()) {
            Aula aulaBD = aulaExistente.get();
            aula.setId(aulaBD.getId());

            return aulaRepository.save(aula);
        }

        throw new AulaNaoEncontrada("Aula não encontrada");
    }

    public void deletarAula(Integer id) {
        aulaRepository.deleteById(id);
    }

    public void desativarAula(Integer id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new AulaNaoEncontrada("Aula não encontrada"));

        aula.setMarcada(false);

        aulaRepository.save(aula);
    }

    public void reativarAula(Integer id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new AulaNaoEncontrada("Aula não encontrada"));

        aula.setMarcada(true);

        aulaRepository.save(aula);
    }
}