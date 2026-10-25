package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.exceptions.ProfessorConflito;
import com.pilates.thais.almeida.exceptions.ProfessorNaoEncontrado;
import com.pilates.thais.almeida.repository.ProfessorRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;

    public ProfessorService(ProfessorRepository professorRepository, TurmaRepository turmaRepository) {
        this.professorRepository = professorRepository;
        this.turmaRepository = turmaRepository;
    }

    public List<Professor> listar(){
        return professorRepository.findAll();

 }
 public Professor cadastrar( Professor request){
        if(professorRepository.existsByEmail(request.getEmail())){
            throw new ProfessorConflito("Já existe professor com esse Email cadastrado");
        }
        return professorRepository.save(request);
 }
 public Professor buscarPorId(Integer id){
        return professorRepository.findById(id).orElseThrow(()->new ProfessorNaoEncontrado("Professor não encontrado"));

 }

    public void inativar(Integer id) {

        Professor professor=professorRepository.findById(id).orElseThrow
                (()->new ProfessorNaoEncontrado("Professor não encontrado"));
        professor.setAtivo(false);
        professorRepository.save(professor);

        List<Turma> turmasDoProfessor = turmaRepository.findByProfessor_IdAndAtivaTrue(id);
        for (Turma turma : turmasDoProfessor) {
            turma.setAtiva(false);
            turmaRepository.save(turma);
        }
    }

    public Professor editar(Professor professorNovo, Integer id) {
        Professor professor=professorRepository.findById(id).orElseThrow(()-> new ProfessorNaoEncontrado(""));

        if (professorRepository.existsByEmailAndIdNot(professorNovo.getEmail(), id)) {
            throw new ProfessorConflito("Já existe professor com esse Email cadastrado");
        }

        professor.setTelefone(professorNovo.getTelefone());
        professor.setNome(professorNovo.getNome());
        professor.setEmail(professorNovo.getEmail());
        professor.setAtivo(professorNovo.getAtivo());
       return professorRepository.save(professor);


    }
}
