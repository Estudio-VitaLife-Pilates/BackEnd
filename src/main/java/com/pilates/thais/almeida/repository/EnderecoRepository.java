package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    Optional<Endereco> findByAlunoId(Integer alunoId);
}
