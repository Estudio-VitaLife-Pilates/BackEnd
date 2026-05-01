package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Integer> {
}
