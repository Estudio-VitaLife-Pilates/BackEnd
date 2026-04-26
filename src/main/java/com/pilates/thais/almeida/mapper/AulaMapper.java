package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.aula.AulaDetailsResponseDto;
import com.pilates.thais.almeida.dto.aula.AulaRequestDto;
import com.pilates.thais.almeida.dto.aula.AulaResponseDto;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.entity.Professor;

import java.util.List;
import java.util.stream.Collectors;

public class AulaMapper {

    public static Aula toEntity(AulaRequestDto dto) {
        Aula aula = new Aula();
        aula.setDataAula(dto.getDataAula());
        aula.setMarcada(dto.getMarcada());

        Turma turma = new Turma();
        turma.setId(dto.getTurmaId());
        aula.setTurma(turma);

        Professor professor = new Professor();
        professor.setId(dto.getProfessorId());
        aula.setProfessor(professor);

        return aula;
    }

    public static AulaDetailsResponseDto toResponseDetails(Aula entity) {
        AulaDetailsResponseDto dto = new AulaDetailsResponseDto();
        dto.setId(entity.getId());
        dto.setDataAula(entity.getDataAula());
        dto.setMarcada(entity.getMarcada());

        Turma turma = entity.getTurma();
        Professor professor = entity.getProfessor();

        String diaSemana = (turma != null && turma.getDiaSemana() != null)
                ? turma.getDiaSemana().toString()
                : "Não atribuída";

        dto.setTurma(diaSemana);
        dto.setProfessor(professor != null ? professor.getNome() : "Não atribuído");

        return dto;
    }

    public static List<AulaDetailsResponseDto> toResponseDetails(List<Aula> entities) {
        return entities.stream()
                .map(AulaMapper::toResponseDetails)
                .collect(Collectors.toList());
    }

    public static List<AulaResponseDto> toResponse(List<Aula> entities) {
        return entities.stream()
                .map(AulaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static AulaResponseDto toResponse(Aula entity) {
        AulaResponseDto dto = new AulaResponseDto();
        dto.setId(entity.getId());
        dto.setDataAula(entity.getDataAula());
        dto.setMarcada(entity.getMarcada());

        return dto;
    }
}