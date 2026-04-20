package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    List<Aluno> findAllByNomeContainingIgnoreCase(String nome);
}
