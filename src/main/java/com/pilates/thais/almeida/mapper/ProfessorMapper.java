package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.professor.ProfessorReponseDto;
import com.pilates.thais.almeida.dto.professor.ProfessorRequestDto;
import com.pilates.thais.almeida.entity.Professor;
import org.springframework.security.core.parameters.P;

import java.util.List;


public class ProfessorMapper {
 public static ProfessorReponseDto toResponse(Professor professor){
     ProfessorReponseDto professorReponseDto=new ProfessorReponseDto();
     professorReponseDto.setAtivo(professor.getAtivo());
     professorReponseDto.setNome(professor.getNome());
     professorReponseDto.setEmail(professor.getEmail());
     professorReponseDto.setTelefone(professor.getTelefone());
     professorReponseDto.setId(professor.getId());
     return professorReponseDto;

 }
 public static Professor toEntity(ProfessorRequestDto professorRequestDto){
     Professor professor=new Professor();
     professor.setNome(professorRequestDto.getNome());
     professor.setAtivo(professorRequestDto.getAtivo());
     professor.setEmail(professorRequestDto.getEmail());
     professor.setTelefone(professorRequestDto.getTelefone());
     return professor;
 }
 public static List<ProfessorReponseDto> toResponse(List<Professor> professores){
    return professores.stream().map(ProfessorMapper::toResponse).toList();
 }
}
