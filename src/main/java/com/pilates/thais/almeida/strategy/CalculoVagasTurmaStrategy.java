package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.Turma;

public interface CalculoVagasTurmaStrategy {

    Integer calcular(Turma turma, Integer vagasOcupadas);
}
