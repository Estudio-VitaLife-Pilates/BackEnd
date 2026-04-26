package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findAllByDataAula(LocalDate dataAula);
}
