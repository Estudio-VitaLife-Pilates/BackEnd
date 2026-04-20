package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> obterTodos(){
        return alunoRepository.findAll();
    }

    public Aluno obterPorId(Integer id){
        return alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNaoEncontrado(""));
    }

    public Aluno criar(Aluno aluno){
        return alunoRepository.save(aluno);
    }

    public void deletarPorId(Integer id){
        alunoRepository.deleteById(id);
    }
}
