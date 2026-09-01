package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AulaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AulaAlunoRepository extends JpaRepository<AulaAluno, Integer> {

    List<AulaAluno> findByAula_Id(Integer aulaId);

    @Query("SELECT COUNT(aa) FROM AulaAluno aa WHERE aa.status = 'AUSENTE' " +
           "AND NOT EXISTS (SELECT 1 FROM AulaAluno r WHERE r.aulaOrigem = aa.aula AND r.aluno = aa.aluno AND r.status = 'REPOSICAO')")
    long countAguardandoReagendamento();

    long countByStatusAndAula_DataAulaBetween(String status, LocalDate inicio, LocalDate fim);
}
