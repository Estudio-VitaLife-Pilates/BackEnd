package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.plano.PlanoRequestDto;
import com.pilates.thais.almeida.dto.plano.PlanoResponseDto;
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
}
