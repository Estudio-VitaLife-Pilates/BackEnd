package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Turma;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeracaoSemanalDatasAulaStrategy implements GeracaoDatasAulaStrategy {

    @Override
    public List<LocalDate> gerarDatas(Turma turma, AlunoPlano alunoPlano, LocalDate dataReferencia) {
        List<LocalDate> datas = new ArrayList<>();
        LocalDate diaTurma = proximaDataDaTurma(turma, dataReferencia);

        while (!diaTurma.isAfter(alunoPlano.getDataFim())) {
            datas.add(diaTurma);
            diaTurma = diaTurma.plusDays(7);
        }

        return datas;
    }

    private LocalDate proximaDataDaTurma(Turma turma, LocalDate dataReferencia) {
        DayOfWeek diaSemanaTurma = paraDayOfWeek(turma.getDiaSemana());
        LocalDate diaTurma = dataReferencia.plusDays(1);

        while (diaTurma.getDayOfWeek() != diaSemanaTurma) {
            diaTurma = diaTurma.plusDays(1);
        }

        return diaTurma;
    }

    private DayOfWeek paraDayOfWeek(Turma.DiaSemana diaSemana) {
        return DayOfWeek.of(diaSemana.ordinal() + 1);
    }
}