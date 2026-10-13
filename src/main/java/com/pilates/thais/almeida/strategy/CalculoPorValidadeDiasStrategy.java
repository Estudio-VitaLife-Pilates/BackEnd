package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.Plano;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CalculoPorValidadeDiasStrategy implements CalculoVigenciaPlanoStrategy {

    @Override
    public LocalDate calcularDataFim(LocalDate dataInicio, Plano plano) {
        return dataInicio.plusDays(plano.getValidadeDias());
    }
}
