package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AlunoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoPlanoRepository  extends JpaRepository<AlunoPlano, Integer> {
}
