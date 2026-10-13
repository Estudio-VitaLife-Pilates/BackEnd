package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AulaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AulaAlunoRepository extends JpaRepository<AulaAluno, Integer> {

    List<AulaAluno> findByAula_Id(Integer aulaId);
    boolean existsByAula_IdAndAluno_Id(Integer aulaId, Integer alunoId);

    List<AulaAluno> findByAluno_IdOrderByAula_DataAulaAsc(Integer alunoId);

    @Query("SELECT COUNT(aa) FROM AulaAluno aa WHERE aa.status = 'AUSENTE' " +
            "AND NOT EXISTS (SELECT 1 FROM AulaAluno r WHERE r.aulaOrigem = aa.aula AND r.aluno = aa.aluno AND r.status = 'REPOSICAO')")
    long countAguardandoReagendamento();

    @Query("SELECT COUNT(aa) FROM AulaAluno aa WHERE aa.aula.id = :aulaId AND aa.status <> 'CANCELADA'")
    Integer countOcupantesAtivosDaAula(@Param("aulaId") Integer aulaId);

    long countByStatusAndAula_DataAulaBetween(String status, LocalDate inicio, LocalDate fim);

    Optional<AulaAluno> findByAula_IdAndAluno_Id(Integer id, Integer alunoId);
}