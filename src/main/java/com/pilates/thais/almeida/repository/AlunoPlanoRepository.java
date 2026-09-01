package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AlunoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoPlanoRepository  extends JpaRepository<AlunoPlano, Integer> {
    Optional<AlunoPlano> findByIdAndAluno_IdAndPlano_Id(Integer id, Integer alunoId, Integer planoId);
    List<AlunoPlano> findByAtivoIsTrueAndAluno_Id(Integer alunoId);

    long countByAtivoFalseAndDataFimBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT COUNT(ap) FROM AlunoPlano ap WHERE ap.dataInicio <= :data AND (ap.dataFim IS NULL OR ap.dataFim >= :data)")
    long countAtivosNaData(@Param("data") LocalDate data);
}
