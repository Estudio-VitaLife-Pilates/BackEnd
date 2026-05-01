package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AlunoTurma;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


public interface AlunoTurmaRepository extends JpaRepository<AlunoTurma,Integer> {
    AlunoTurma findByAlunoIdAndTurmaId(Integer alunoId, Integer id);
AlunoTurma findByTurmaIdAndAtivoTrue(Integer id);
@Transactional
@Modifying
    void removeById(Integer id);

    Integer countByTurmaIdAndAtivoTrue(Integer id);
}
