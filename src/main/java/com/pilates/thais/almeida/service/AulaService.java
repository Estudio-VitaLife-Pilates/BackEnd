package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.*;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.exceptions.AulaNaoEncontrada;
import com.pilates.thais.almeida.exceptions.ProfessorNaoEncontrado;
import com.pilates.thais.almeida.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final AulaAlunoRepository aulaAlunoRepository;
    private final AlunoRepository alunoRepository;

    public AulaService(AulaRepository aulaRepository, TurmaRepository turmaRepository, ProfessorRepository professorRepository, AulaAlunoRepository aulaAlunoRepository, AlunoRepository alunoRepository) {
        this.aulaRepository = aulaRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.aulaAlunoRepository = aulaAlunoRepository;
        this.alunoRepository = alunoRepository;
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

    public Aula criarAula(Integer turmaId, Integer professorId, LocalDate dia, Aluno aluno) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aula aula = new Aula();

        aula.setTurma(turma);
        aula.setProfessor(professor);
        aula.setDataAula(dia);
        aula.setMarcada(true);

        // criando aulaaluno - precisa fazer para o outro metodo tb ok kk precisa pensar dps talvez precisa dar uma mudada nos testes afs vei
        AulaAluno aulaAluno = new AulaAluno();

        aulaAluno.setAula(aula);
        aulaAluno.setAluno(aluno);
        aulaAluno.setAulaOrigem(aula);

        Aula aulaSaved = aulaRepository.save(aula);
        aulaAlunoRepository.save(aulaAluno);
        return aulaSaved;
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

    public Aula trocarProfessor(
            Integer idAula,
            Integer idProfessor
    ){
        Aula aula = aulaRepository.findById(idAula)
                .orElseThrow(() -> new AulaNaoEncontrada("Aula não encontrada"));

        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new ProfessorNaoEncontrado("Professor não encontrado"));

        aula.setProfessor(professor);

        return aulaRepository.save(aula);
    }
    public Aula buscarProximaAulaDaTurma(Integer turmaId) {
        return aulaRepository
                .findFirstByTurma_IdAndDataAulaGreaterThanEqualOrderByDataAulaAsc(turmaId, LocalDate.now())
                .orElse(null);
    }

    public List<AulaAluno> listarAlunosDaAula(Integer aulaId) {
        return aulaAlunoRepository.findByAula_Id(aulaId);
    }

    public void removerAlunoDaAula(Integer aulaAlunoId) {
        aulaAlunoRepository.deleteById(aulaAlunoId);
    }
    public List<AulaAluno> listarAulasDoAluno(Integer alunoId) {
        return aulaAlunoRepository.findByAluno_IdOrderByAula_DataAulaDesc(alunoId);
    }

    public void cancelarAulaDoAluno(Integer aulaAlunoId) {
        AulaAluno aulaAluno = aulaAlunoRepository.findById(aulaAlunoId)
                .orElseThrow(() -> new AulaNaoEncontrada("Registro de aula não encontrado"));
        aulaAluno.setStatus("CANCELADA");
        aulaAlunoRepository.save(aulaAluno);
    }

    public AulaAluno registrarReposicao(Integer aulaDestinoId, Integer aulaOrigemId, Integer alunoId) {
        Aula aulaDestino = aulaRepository.findById(aulaDestinoId)
                .orElseThrow(() -> new AulaNaoEncontrada("Aula de destino não encontrada"));
        Aula aulaOrigem = aulaRepository.findById(aulaOrigemId)
                .orElseThrow(() -> new AulaNaoEncontrada("Aula de origem não encontrada"));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontrado("Aluno não encontrado"));

        AulaAluno aulaAluno = new AulaAluno();
        aulaAluno.setAula(aulaDestino);
        aulaAluno.setAluno(aluno);
        aulaAluno.setAulaOrigem(aulaOrigem);
        aulaAluno.setStatus("REPOSICAO");

        return aulaAlunoRepository.save(aulaAluno);
    }
}