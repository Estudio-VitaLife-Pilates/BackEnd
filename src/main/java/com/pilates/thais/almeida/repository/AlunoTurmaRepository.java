package com.pilates.thais.almeida.repository;

import com.pilates.thais.almeida.entity.AlunoTurma;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlunoTurmaRepository extends JpaRepository<AlunoTurma,Integer> {
    AlunoTurma findByAlunoIdAndTurmaId(Integer alunoId, Integer id);
    
    AlunoTurma findByTurmaIdAndAtivoTrue(Integer id);
    
    @Transactional
    @Modifying
    void removeById(Integer id);

    Integer countByTurmaIdAndAtivoTrue(Integer id);

    boolean existsByAlunoIdAndTurmaId(Integer id, Integer id1);

    Integer countByAlunoId(Integer alunoId);

    @Query("SELECT at.turma.id, COUNT(at) FROM AlunoTurma at WHERE at.ativo = true GROUP BY at.turma.id")
    List<Object[]> countMatriculadosPorTurma();
}
