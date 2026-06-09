package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Turma;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class GeracaoSemanalDatasAulaStrategy implements GeracaoDatasAulaStrategy {

    @Override
    public List<LocalDate> gerarDatas(Turma turma, AlunoPlano alunoPlano, LocalDate dataReferencia) {
        List<LocalDate> datas = new ArrayList<>();
        LocalDate diaTurma = proximaDataDaTurma(turma, dataReferencia);

        while(!diaTurma.isAfter(alunoPlano.getDataFim())){
            datas.add(diaTurma);
            diaTurma = diaTurma.plusDays(7);
        }

        return datas;
    }

    private LocalDate proximaDataDaTurma(Turma turma, LocalDate dataReferencia) {
        String diaSemanaTurma = String.valueOf(turma.getDiaSemana());
        LocalDate diaTurma = dataReferencia.plusDays(1);

        while(!obterDiaSemana(diaTurma).equals(diaSemanaTurma)){
            diaTurma = diaTurma.plusDays(1);
        }

        return diaTurma;
    }

    private String obterDiaSemana(LocalDate data){
        return data.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).split("-")[0].toUpperCase();
    }
}
