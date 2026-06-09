package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.Plano;

import java.time.LocalDate;

public interface CalculoVigenciaPlanoStrategy {

    LocalDate calcularDataFim(LocalDate dataInicio, Plano plano);
}
