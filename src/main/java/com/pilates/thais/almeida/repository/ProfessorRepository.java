package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor,Integer>{

    boolean existsByEmail(String email);
}
