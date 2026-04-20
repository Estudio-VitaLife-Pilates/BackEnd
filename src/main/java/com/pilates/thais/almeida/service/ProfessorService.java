package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.exceptions.ProfessorConflito;
import com.pilates.thais.almeida.exceptions.ProfessorNaoEncontrado;
import com.pilates.thais.almeida.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
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

    public void inativar(Integer id) {

        Professor professor=professorRepository.findById(id).orElseThrow
                (()->new ProfessorNaoEncontrado("Professor não encontrado"));
        professor.setAtivo(false);
        professorRepository.save(professor);
    }

    public Professor editar(Professor professorNovo, Integer id) {
        Professor professor=professorRepository.findById(id).orElseThrow(()-> new ProfessorNaoEncontrado(""));
        professor.setTelefone(professorNovo.getTelefone());
        professor.setNome(professorNovo.getNome());
        professor.setEmail(professorNovo.getEmail());
        professor.setAtivo(professorNovo.getAtivo());
       return professorRepository.save(professor);


    }
}
