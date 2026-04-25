package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AulaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AulaAlunoRepository extends JpaRepository<AulaAluno, Integer> {
}
