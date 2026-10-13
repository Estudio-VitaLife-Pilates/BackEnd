package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Turma;

import java.time.LocalDate;
import java.util.List;

public interface GeracaoDatasAulaStrategy {

    List<LocalDate> gerarDatas(Turma turma, AlunoPlano alunoPlano, LocalDate dataReferencia);
}
