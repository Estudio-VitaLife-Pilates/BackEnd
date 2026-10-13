package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.Turma;
import org.springframework.stereotype.Component;

@Component
public class CalculoVagasPorCapacidadeStrategy implements CalculoVagasTurmaStrategy {

    @Override
    public Integer calcular(Turma turma, Integer vagasOcupadas) {
        return turma.getCapacidadeMax() - vagasOcupadas;
    }
}
