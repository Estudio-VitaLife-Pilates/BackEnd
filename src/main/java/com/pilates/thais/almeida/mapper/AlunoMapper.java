package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.entity.Aluno;

import java.time.LocalDate;
import java.util.List;

public class AlunoMapper {

    public static AlunoResponseDto toResponse(Aluno entity){
        AlunoResponseDto dto = new AlunoResponseDto();

        dto.setNome(entity.getNome());
        dto.setCpf(entity.getCpf());
        dto.setEmail(entity.getEmail());
        dto.setAtivo(entity.getAtivo());
        dto.setDataCadastro(entity.getDataCadastro());
        dto.setId(entity.getId());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setTelefone(entity.getTelefone());
        dto.setFichaAnamnese(entity.getFichaAnamnese());

        return dto;
    }

    public static List<AlunoResponseDto> toResponse(List<Aluno> entities){
        return entities.stream()
                .map(AlunoMapper::toResponse)
                .toList();
    }

    public static Aluno toEntity(AlunoRequestDto dto){
        Aluno aluno = new Aluno();

        aluno.setNome(dto.getNome());
        aluno.setAtivo(true);
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataCadastro(LocalDate.now());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setFichaAnamnese(dto.getFichaAnamnese());

        return aluno;
    }
}
