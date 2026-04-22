package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AlunoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoPlanoRepository  extends JpaRepository<AlunoPlano, Integer> {
    Optional<AlunoPlano> findByIdAndAluno_IdAndPlano_Id(Integer id, Integer alunoId, Integer planoId);
}
