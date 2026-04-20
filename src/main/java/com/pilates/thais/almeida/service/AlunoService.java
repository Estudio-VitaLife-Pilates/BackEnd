package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> obterTodos(){
        return alunoRepository.findAll();
    }
}
