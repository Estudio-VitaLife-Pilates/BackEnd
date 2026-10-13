package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.dashboard.AlunoRecenteResponseDto;
import com.pilates.thais.almeida.dto.dashboard.AulaHojeResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DashboardAlertaResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DashboardKpisResponseDto;
import com.pilates.thais.almeida.dto.dashboard.DesempenhoMensalResponseDto;
import com.pilates.thais.almeida.dto.dashboard.MetricasAlunoResponseDto;
import com.pilates.thais.almeida.dto.dashboard.MetricasAulaResponseDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Aula;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.AlunoTurmaRepository;
import com.pilates.thais.almeida.repository.AulaAlunoRepository;
import com.pilates.thais.almeida.repository.AulaRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetricasService {

    private final AlunoRepository alunoRepository;
    private final AulaRepository aulaRepository;
    private final AulaAlunoRepository aulaAlunoRepository;
    private final AlunoPlanoRepository alunoPlanoRepository;
    private final AlunoTurmaRepository alunoTurmaRepository;
    private final TurmaRepository turmaRepository;

    public MetricasService(AlunoRepository alunoRepository, AulaRepository aulaRepository,
                            AulaAlunoRepository aulaAlunoRepository, AlunoPlanoRepository alunoPlanoRepository,
                            AlunoTurmaRepository alunoTurmaRepository, TurmaRepository turmaRepository) {
        this.alunoRepository = alunoRepository;
        this.aulaRepository = aulaRepository;
        this.aulaAlunoRepository = aulaAlunoRepository;
        this.alunoPlanoRepository = alunoPlanoRepository;
        this.alunoTurmaRepository = alunoTurmaRepository;
        this.turmaRepository = turmaRepository;
    }

    public DashboardKpisResponseDto obterKpis() {
        LocalDate hoje = hoje();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = inicioMes.plusMonths(1).minusDays(1);
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = inicioSemana.plusDays(6);

        long totalAlunos = alunoRepository.countByAtivoTrue();
        long novosAlunosMes = alunoRepository.countByDataCadastroBetween(inicioMes, fimMes);

        long aulasProgramadasSemana = aulaRepository.countByDataAulaBetweenAndMarcadaTrue(inicioSemana, fimSemana);
        long aulasCanceladasSemana = aulaRepository.countByDataAulaBetweenAndMarcadaFalse(inicioSemana, fimSemana);
        long aulasSemana = aulasProgramadasSemana + aulasCanceladasSemana;

        double taxaOcupacao = calcularTaxaOcupacao();

        long ativosInicioMes = alunoPlanoRepository.countAtivosNaData(inicioMes);
        long desmatriculadosMes = alunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMes, fimMes);
        double taxaSaidaMes = ativosInicioMes == 0 ? 0.0 : (double) desmatriculadosMes / ativosInicioMes;
        double taxaRetencao = ativosInicioMes == 0 ? 0.0 : 1 - taxaSaidaMes;

        // sem histórico mensal de ocupação no schema atual: fica 0.0 até termos um snapshot por mês
        double variacaoOcupacaoMes = 0.0;

        return new DashboardKpisResponseDto(totalAlunos, novosAlunosMes, taxaOcupacao, variacaoOcupacaoMes,
                aulasSemana, aulasCanceladasSemana, aulasProgramadasSemana, taxaRetencao, taxaSaidaMes);
    }

    public DashboardAlertaResponseDto obterAlertas() {
        LocalDate hoje = hoje();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = inicioMes.plusMonths(1).minusDays(1);

        long alunosParaReagendamento = aulaAlunoRepository.countAguardandoReagendamento();
        long aulasCanceladasMes = aulaRepository.countByDataAulaBetweenAndMarcadaFalse(inicioMes, fimMes);
        long aulasRemarcadasMes = aulaAlunoRepository.countByStatusAndAula_DataAulaBetween("REPOSICAO", inicioMes, fimMes);
        long novosAlunosMes = alunoRepository.countByDataCadastroBetween(inicioMes, fimMes);
        long desmatriculasMes = alunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMes, fimMes);

        return new DashboardAlertaResponseDto(alunosParaReagendamento, aulasCanceladasMes, aulasRemarcadasMes,
                novosAlunosMes, desmatriculasMes);
    }

    public List<DesempenhoMensalResponseDto> obterDesempenho(String periodo) {
        if ("semanal".equalsIgnoreCase(periodo)) {
            return obterDesempenhoSemanal();
        }
        return obterDesempenhoMensal();
    }

    public List<AlunoRecenteResponseDto> obterAlunosRecentes(int limit) {
        List<Aluno> alunos = alunoRepository.findTop5ByOrderByDataCadastroDesc();

        List<AlunoRecenteResponseDto> resultado = new ArrayList<>();
        for (Aluno aluno : alunos) {
            if (resultado.size() >= limit) {
                break;
            }
            resultado.add(mapearAlunoRecente(aluno));
        }
        return resultado;
    }

    public List<AulaHojeResponseDto> obterAulasHoje(String periodo) {
        LocalDate hoje = hoje();
        List<Aula> aulas;

        if ("semana".equalsIgnoreCase(periodo)) {
            LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
            aulas = aulaRepository.findAllByDataAulaBetween(inicioSemana, inicioSemana.plusDays(6));
        } else {
            aulas = aulaRepository.findAllByDataAula(hoje);
        }

        List<AulaHojeResponseDto> resultado = new ArrayList<>();
        for (Aula aula : aulas) {
            resultado.add(mapearAulaHoje(aula));
        }
        return resultado;
    }

    public MetricasAlunoResponseDto obterMetricasAlunos(YearMonth mes) {
        LocalDate inicioMes = mes.atDay(1);
        LocalDate fimMes = mes.atEndOfMonth();
        LocalDate inicioMesAnterior = mes.minusMonths(1).atDay(1);
        LocalDate fimMesAnterior = mes.minusMonths(1).atEndOfMonth();

        long novosAlunos = alunoRepository.countByDataCadastroBetween(inicioMes, fimMes);
        long novosAlunosMesAnterior = alunoRepository.countByDataCadastroBetween(inicioMesAnterior, fimMesAnterior);
        long variacaoNovosAlunos = novosAlunos - novosAlunosMesAnterior;

        long desmatriculados = alunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMes, fimMes);
        long desmatriculadosMesAnterior = alunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMesAnterior, fimMesAnterior);
        long variacaoDesmatriculados = desmatriculados - desmatriculadosMesAnterior;

        long paraReagendamento = aulaAlunoRepository.countAguardandoReagendamento();

        long ativosInicioMes = alunoPlanoRepository.countAtivosNaData(inicioMes);
        double taxaSaida = ativosInicioMes == 0 ? 0.0 : (double) desmatriculados / ativosInicioMes;

        return new MetricasAlunoResponseDto(novosAlunos, variacaoNovosAlunos, desmatriculados,
                variacaoDesmatriculados, paraReagendamento, taxaSaida);
    }

    public MetricasAulaResponseDto obterMetricasAulas(YearMonth mes) {
        LocalDate inicioMes = mes.atDay(1);
        LocalDate fimMes = mes.atEndOfMonth();

        long aulasRealizadas = aulaRepository.countByDataAulaBetweenAndMarcadaTrue(inicioMes, fimMes);
        long aulasCanceladas = aulaRepository.countByDataAulaBetweenAndMarcadaFalse(inicioMes, fimMes);
        long aulasProgramadas = aulasRealizadas + aulasCanceladas;
        long aulasRemarcadas = aulaAlunoRepository.countByStatusAndAula_DataAulaBetween("REPOSICAO", inicioMes, fimMes);

        double taxaRemarcacao = aulasProgramadas == 0 ? 0.0 : (double) aulasRemarcadas / aulasProgramadas;

        return new MetricasAulaResponseDto(aulasCanceladas, aulasRemarcadas, taxaRemarcacao,
                aulasRealizadas, aulasProgramadas);
    }

    private double calcularTaxaOcupacao() {
        Map<Integer, Long> matriculadosPorTurma = new LinkedHashMap<>();
        for (Object[] linha : alunoTurmaRepository.countMatriculadosPorTurma()) {
            matriculadosPorTurma.put((Integer) linha[0], (Long) linha[1]);
        }

        double somaOcupacao = 0.0;
        int turmasConsideradas = 0;

        for (Turma turma : turmaRepository.findTurmasByAtivaTrue()) {
            Integer capacidadeMax = turma.getCapacidadeMax();
            if (capacidadeMax == null || capacidadeMax == 0) {
                continue;
            }
            long matriculados = matriculadosPorTurma.getOrDefault(turma.getId(), 0L);
            somaOcupacao += (double) matriculados / capacidadeMax;
            turmasConsideradas++;
        }

        return turmasConsideradas == 0 ? 0.0 : somaOcupacao / turmasConsideradas;
    }

    private List<DesempenhoMensalResponseDto> obterDesempenhoMensal() {
        LocalDate hoje = hoje();
        LocalDate desde = hoje.minusMonths(5).withDayOfMonth(1);

        Map<String, long[]> porMes = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            porMes.put(YearMonth.from(hoje.minusMonths(i)).toString(), new long[]{0L, 0L});
        }

        for (Object[] linha : aulaRepository.agruparPorMesEStatus(desde)) {
            String mes = (String) linha[0];
            boolean marcada = Boolean.TRUE.equals(linha[1]);
            long total = (Long) linha[2];

            long[] contadores = porMes.get(mes);
            if (contadores == null) {
                continue;
            }
            if (marcada) {
                contadores[0] += total;
            } else {
                contadores[1] += total;
            }
        }

        List<DesempenhoMensalResponseDto> resultado = new ArrayList<>();
        for (Map.Entry<String, long[]> entrada : porMes.entrySet()) {
            resultado.add(new DesempenhoMensalResponseDto(entrada.getKey(), entrada.getValue()[0], entrada.getValue()[1]));
        }
        return resultado;
    }

    private List<DesempenhoMensalResponseDto> obterDesempenhoSemanal() {
        LocalDate inicioSemanaAtual = hoje().with(DayOfWeek.MONDAY);

        List<DesempenhoMensalResponseDto> resultado = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            LocalDate inicio = inicioSemanaAtual.minusWeeks(i);
            LocalDate fim = inicio.plusDays(6);

            long realizadas = aulaRepository.countByDataAulaBetweenAndMarcadaTrue(inicio, fim);
            long canceladas = aulaRepository.countByDataAulaBetweenAndMarcadaFalse(inicio, fim);

            resultado.add(new DesempenhoMensalResponseDto(inicio.toString(), realizadas, canceladas));
        }
        return resultado;
    }

    private AlunoRecenteResponseDto mapearAlunoRecente(Aluno aluno) {
        AlunoPlano planoAtivo = null;
        for (AlunoPlano alunoPlano : aluno.getAlunoPlanos()) {
            if (Boolean.TRUE.equals(alunoPlano.getAtivo())) {
                planoAtivo = alunoPlano;
                break;
            }
        }

        String nomePlano = planoAtivo != null ? planoAtivo.getPlano().getNome() : null;
        Integer frequenciaSemanal = planoAtivo != null ? planoAtivo.getPlano().getFrequenciaSemanal() : null;

        return new AlunoRecenteResponseDto(aluno.getId(), aluno.getNome(), aluno.getTelefone(),
                nomePlano, frequenciaSemanal, aluno.getDataCadastro());
    }

    private AulaHojeResponseDto mapearAulaHoje(Aula aula) {
        Turma turma = aula.getTurma();
        LocalTime horaInicio = turma != null ? turma.getHoraInicio() : null;
        String turmaDiaSemana = turma != null && turma.getDiaSemana() != null ? turma.getDiaSemana().name() : null;
        String professorNome = aula.getProfessor() != null ? aula.getProfessor().getNome() : null;
        long totalAlunos = aula.getAlunos() == null ? 0 : aula.getAlunos().size();

        return new AulaHojeResponseDto(aula.getId(), horaInicio, professorNome, turmaDiaSemana, totalAlunos);
    }

    private LocalDate hoje() {
        return LocalDate.now();
    }
}
