package com.pilates.thais.almeida.strategy;

import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Turma;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeracaoSemanalDatasAulaStrategyTest {

    private final GeracaoSemanalDatasAulaStrategy strategy = new GeracaoSemanalDatasAulaStrategy();

    private AlunoPlano criarAlunoPlanoComFim(LocalDate dataFim) {
        AlunoPlano alunoPlano = new AlunoPlano();
        alunoPlano.setDataFim(dataFim);
        return alunoPlano;
    }

    @Test
    @DisplayName("Deve incluir o proprio dia de hoje quando o aluno entra no dia da semana da turma")
    void deveIncluirHojeQuandoDataReferenciaForODiaDaTurma() {
        LocalDate hoje = LocalDate.of(2026, 10, 26); // segunda-feira
        assertEquals(DayOfWeek.MONDAY, hoje.getDayOfWeek());

        Turma turma = new Turma(Turma.DiaSemana.SEGUNDA, LocalTime.of(8, 0), 60, 10, true);
        AlunoPlano alunoPlano = criarAlunoPlanoComFim(hoje.plusWeeks(2));

        List<LocalDate> datas = strategy.gerarDatas(turma, alunoPlano, hoje);

        assertEquals(hoje, datas.get(0));
    }

    @Test
    @DisplayName("Deve começar no proximo dia da semana da turma quando hoje nao for esse dia")
    void deveComecarNoProximoDiaDaTurmaQuandoHojeNaoForEsseDia() {
        LocalDate hoje = LocalDate.of(2026, 10, 26); // segunda-feira

        Turma turma = new Turma(Turma.DiaSemana.QUARTA, LocalTime.of(8, 0), 60, 10, true);
        AlunoPlano alunoPlano = criarAlunoPlanoComFim(hoje.plusWeeks(2));

        List<LocalDate> datas = strategy.gerarDatas(turma, alunoPlano, hoje);

        assertEquals(LocalDate.of(2026, 10, 28), datas.get(0)); // próxima quarta-feira
    }

    @Test
    @DisplayName("Deve gerar uma data por semana ate a vigencia do plano")
    void deveGerarUmaDataPorSemanaAteAVigenciaDoPlano() {
        LocalDate hoje = LocalDate.of(2026, 10, 26); // segunda-feira

        Turma turma = new Turma(Turma.DiaSemana.SEGUNDA, LocalTime.of(8, 0), 60, 10, true);
        AlunoPlano alunoPlano = criarAlunoPlanoComFim(hoje.plusWeeks(3));

        List<LocalDate> datas = strategy.gerarDatas(turma, alunoPlano, hoje);

        assertEquals(4, datas.size());
        assertEquals(hoje, datas.get(0));
        assertEquals(hoje.plusWeeks(1), datas.get(1));
        assertEquals(hoje.plusWeeks(2), datas.get(2));
        assertEquals(hoje.plusWeeks(3), datas.get(3));
    }
}
