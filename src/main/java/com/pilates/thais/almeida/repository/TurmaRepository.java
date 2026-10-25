package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma,Integer> {


    List<Turma> findTurmasByAtivaTrue();

    List<Turma> findByDiaSemanaIgnoreCase(String diaSemana);

    boolean existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrue(LocalTime horaInicio, Turma.DiaSemana diaSemana, Integer professorId);

    boolean existsByHoraInicioAndDiaSemanaAndProfessor_IdAndAtivaTrueAndIdNot(LocalTime horaInicio, Turma.DiaSemana diaSemana, Integer professorId, Integer id);

    List<Turma> findByProfessor_Id(Integer professorId);

    List<Turma> findByProfessor_IdAndAtivaTrue(Integer professorId);
}
