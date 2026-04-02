package almeida.thais.pilates.repository.professor;

import almeida.thais.pilates.model.professor.Professor;
import org.hibernate.id.IncrementGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}
