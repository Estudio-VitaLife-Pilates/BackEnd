package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.plano.PlanoDetailsResponseDto;
import com.pilates.thais.almeida.dto.plano.PlanoRequestDto;
import com.pilates.thais.almeida.dto.plano.PlanoResponseDto;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Plano;

import java.util.List;

public class PlanoMapper {

    public static Plano toEntity(PlanoRequestDto dto){
        Plano plano = new Plano();

        plano.setNome(dto.getNome());
        plano.setFrequenciaSemanal(dto.getFrequenciaSemanal());
        plano.setValidadeDias(dto.getValidadeDias());
        plano.setValorMensal(dto.getValorMensal());

        return plano;
    }

    public static PlanoResponseDto toResponse(Plano entity){
        PlanoResponseDto dto = new PlanoResponseDto();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setValidadeDias(entity.getValidadeDias());
        dto.setFrequenciaSemanal(entity.getFrequenciaSemanal());
        dto.setValorMensal(entity.getValorMensal());

        return dto;
    }

    public static List<PlanoResponseDto> toResponse(List<Plano> entities){
        return entities.stream()
                .map(PlanoMapper::toResponse)
                .toList();
    }

    private static PlanoDetailsResponseDto.PlanoAlunoResponseDto toAlunoResponse(AlunoPlano entity){
        PlanoDetailsResponseDto.PlanoAlunoResponseDto dto = new PlanoDetailsResponseDto.PlanoAlunoResponseDto();

        dto.setNome(entity.getAluno().getNome());
        dto.setCpf(entity.getAluno().getCpf());
        dto.setEmail(entity.getAluno().getEmail());
        dto.setTelefone(entity.getAluno().getTelefone());
        dto.setIdAluno(entity.getAluno().getId());
        dto.setAlunoAtivo(entity.getAluno().getAtivo());
        dto.setDataCadastroAluno(entity.getAluno().getDataCadastro());
        dto.setDataNascimentoAluno(entity.getAluno().getDataNascimento());
        dto.setFichaAnamnese(entity.getAluno().getFichaAnamnese());

        dto.setIdPlanoAluno(entity.getId());
        dto.setDataFim(entity.getDataFim());
        dto.setDataInicio(entity.getDataInicio());
        dto.setPlanoAtivo(entity.getAtivo());

        return dto;
    }

    public static PlanoDetailsResponseDto toResponseDetails(Plano entity){
        PlanoDetailsResponseDto dto = new PlanoDetailsResponseDto();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setValidadeDias(entity.getValidadeDias());
        dto.setFrequenciaSemanal(entity.getFrequenciaSemanal());
        dto.setValorMensal(entity.getValorMensal());

        dto.setAlunos(
                entity.getAlunoPlanos()
                        .stream()
                        .map(PlanoMapper::toAlunoResponse)
                        .toList()
        );

        return dto;
    }

    public static List<PlanoDetailsResponseDto> toResponseDetails(List<Plano> entities){
        return entities.stream()
                .map(PlanoMapper::toResponseDetails)
                .toList();
    }
}
