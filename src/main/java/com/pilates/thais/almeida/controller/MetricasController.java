package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.dashboard.AlunoRecenteResponseDto;
import com.pilates.thais.almeida.dto.dashboard.AulaHojeResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DashboardAlertaResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DashboardKpisResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DesempenhoMensalResponseDto;
import com.pilates.thais.almeida.dto.dashboard.MetricasAlunoResponseDto;
import com.pilates.thais.almeida.dto.dashboard.MetricasAulaResponseDto;
import com.pilates.thais.almeida.service.MetricasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@Tag(name = "Dashboard", description = "Métricas e KPIs do painel")
@RequestMapping("/dashboard")
public class MetricasController {

    private final MetricasService metricasService;

    public MetricasController(MetricasService metricasService) {
        this.metricasService = metricasService;
    }

    @Operation(summary = "KPIs do dashboard", description = "Total de alunos, taxa de ocupação, aulas da semana e retenção")
    @GetMapping("/kpis")
    public ResponseEntity<DashboardKpisResponseDto> obterKpis() {
        return ResponseEntity.status(200).body(metricasService.obterKpis());
    }

    @Operation(summary = "Alertas do dashboard", description = "Reagendamentos pendentes, cancelamentos/remarcações e novos alunos do mês")
    @GetMapping("/alertas")
    public ResponseEntity<DashboardAlertaResponseDto> obterAlertas() {
        return ResponseEntity.status(200).body(metricasService.obterAlertas());
    }

    @Operation(summary = "Desempenho de aulas", description = "Série de aulas realizadas x canceladas, por semana ou por mês")
    @GetMapping("/desempenho")
    public ResponseEntity<List<DesempenhoMensalResponseDto>> obterDesempenho(
            @RequestParam(defaultValue = "mensal") String periodo) {
        return ResponseEntity.status(200).body(metricasService.obterDesempenho(periodo));
    }

    @Operation(summary = "Alunos recentes", description = "Últimos alunos cadastrados, com o plano ativo")
    @GetMapping("/alunos-recentes")
    public ResponseEntity<List<AlunoRecenteResponseDto>> obterAlunosRecentes(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.status(200).body(metricasService.obterAlunosRecentes(limit));
    }

    @Operation(summary = "Aulas de hoje ou da semana", description = "Aulas do dia ou da semana, com professor, turma e contagem de alunos")
    @GetMapping("/aulas-hoje")
    public ResponseEntity<List<AulaHojeResponseDto>> obterAulasHoje(
            @RequestParam(defaultValue = "hoje") String periodo) {
        return ResponseEntity.status(200).body(metricasService.obterAulasHoje(periodo));
    }

    @Operation(summary = "Métricas de alunos do mês", description = "Novos, desmatriculados, para reagendamento e taxa de saída (mês no formato yyyy-MM, padrão mês atual)")
    @GetMapping("/metricas-alunos")
    public ResponseEntity<MetricasAlunoResponseDto> obterMetricasAlunos(
            @RequestParam(required = false) String mes) {
        return ResponseEntity.status(200).body(metricasService.obterMetricasAlunos(parseMes(mes)));
    }

    @Operation(summary = "Métricas de aulas do mês", description = "Canceladas, remarcadas, taxa de remarcação e realizadas (mês no formato yyyy-MM, padrão mês atual)")
    @GetMapping("/metricas-aulas")
    public ResponseEntity<MetricasAulaResponseDto> obterMetricasAulas(
            @RequestParam(required = false) String mes) {
        return ResponseEntity.status(200).body(metricasService.obterMetricasAulas(parseMes(mes)));
    }

    private YearMonth parseMes(String mes) {
        return mes == null || mes.isBlank() ? YearMonth.now() : YearMonth.parse(mes);
    }
}
