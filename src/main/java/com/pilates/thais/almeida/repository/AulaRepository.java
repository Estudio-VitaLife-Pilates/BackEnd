package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findAllByDataAula(LocalDate dataAula);

    boolean existsAulaByDataAulaAndTurma_Id(LocalDate dataAula, Integer turmaId);

    boolean existsAulaById(Integer id);

    List<Aula> findAllByDataAulaAfterAndTurma_Id(LocalDate dataAulaAfter, Integer turmaId);

    long countByDataAulaBetweenAndMarcadaTrue(LocalDate inicio, LocalDate fim);

    long countByDataAulaBetweenAndMarcadaFalse(LocalDate inicio, LocalDate fim);

    @Query("SELECT FUNCTION('DATE_FORMAT', a.dataAula, '%Y-%m'), a.marcada, COUNT(a) " +
           "FROM Aula a WHERE a.dataAula >= :desde GROUP BY FUNCTION('DATE_FORMAT', a.dataAula, '%Y-%m'), a.marcada")
    List<Object[]> agruparPorMesEStatus(@Param("desde") LocalDate desde);
}
