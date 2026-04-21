package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.professor.ProfessorReponseDto;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;

import java.util.List;

public class TurmaMapper {
    public static TurmaResponseDto toResponse (Turma turma){

        return new TurmaResponseDto(turma.getId(), turma.getDiaSemana(),turma.getHoraInicio(),turma.getDuracaoMinutos(),
        turma.getAtiva(),turma.getCapacidadeMax());
    }
    public static Turma toEntity(TurmaRequestDto turmaRequestDto){
        return new Turma(turmaRequestDto.getDiaSemana(),turmaRequestDto.getHoraInicio(),turmaRequestDto.getDuracaoMinutos(),turmaRequestDto.getCapacidadeMax(),turmaRequestDto.getAtiva());
    }
    public static List<TurmaResponseDto> toResponse(List<Turma> turmas){
       return turmas.stream().map(TurmaMapper::toResponse).toList();
    }
}
