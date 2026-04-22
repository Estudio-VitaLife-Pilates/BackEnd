package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.aluno.AlunoDetailsResponseDto;
import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
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

    public static AlunoDetailsResponseDto toResponseDetails(Aluno entity){
        AlunoDetailsResponseDto dto = new AlunoDetailsResponseDto();

        dto.setNome(entity.getNome());
        dto.setCpf(entity.getCpf());
        dto.setEmail(entity.getEmail());
        dto.setAtivo(entity.getAtivo());
        dto.setDataCadastro(entity.getDataCadastro());
        dto.setId(entity.getId());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setTelefone(entity.getTelefone());
        dto.setFichaAnamnese(entity.getFichaAnamnese());

        dto.setPlanos(
                entity.getAlunoPlanos()
                        .stream()
                        .map(AlunoMapper::toPlanoResponse)
                        .toList()
        );

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

    private static AlunoDetailsResponseDto.AlunoPlanoResponseDto toPlanoResponse(AlunoPlano entity){

        AlunoDetailsResponseDto.AlunoPlanoResponseDto dto =
                new AlunoDetailsResponseDto.AlunoPlanoResponseDto();

        dto.setIdPlano(entity.getPlano().getId());
        dto.setIdAlunoPlano(entity.getId());
        dto.setNome(entity.getPlano().getNome());
        dto.setFrequenciaSemanal(entity.getPlano().getFrequenciaSemanal());
        dto.setValidadeDias(entity.getPlano().getValidadeDias());
        dto.setValorMensal(entity.getPlano().getValorMensal());

        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());
        dto.setAtivo(entity.getAtivo());

        return dto;
    }

    public static List<AlunoDetailsResponseDto> toResponseDetails(List<Aluno> entities){
        return entities.stream()
                .map(AlunoMapper::toResponseDetails)
                .toList();
    }
}
