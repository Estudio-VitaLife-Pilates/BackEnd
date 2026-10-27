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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        long turmasRealizadasSemana = contarTurmasComAulaJaEncerradaNoPeriodo(inicioSemana, fimSemana);
        long turmasAgendadasSemana = aulaRepository.countDistinctTurmaByDataAulaBetweenAndMarcadaTrue(inicioSemana, fimSemana);

        double taxaOcupacao = calcularTaxaOcupacao();

        long ativosInicioMes = alunoPlanoRepository.countAtivosNaData(inicioMes);
        long desmatriculadosMes = alunoPlanoRepository.countByAtivoFalseAndDataFimBetween(inicioMes, fimMes);
        double taxaSaidaMes = ativosInicioMes == 0 ? 0.0 : (double) desmatriculadosMes / ativosInicioMes;
        double taxaRetencao = 1 - taxaSaidaMes;

        // sem histórico mensal de ocupação no schema atual: fica 0.0 até termos um snapshot por mês
        double variacaoOcupacaoMes = 0.0;

        return new DashboardKpisResponseDto(totalAlunos, novosAlunosMes, taxaOcupacao, variacaoOcupacaoMes,
                aulasSemana, aulasCanceladasSemana, aulasProgramadasSemana, turmasRealizadasSemana,
                turmasAgendadasSemana, taxaRetencao, taxaSaidaMes);
    }

    private long contarTurmasComAulaJaEncerradaNoPeriodo(LocalDate inicio, LocalDate fim) {
        return aulaRepository.findAllByDataAulaBetween(inicio, fim).stream()
                .filter(this::aulaMarcadaEJaEncerrada)
                .map(aula -> aula.getTurma().getId())
                .collect(Collectors.toSet())
                .size();
    }

    private boolean aulaMarcadaEJaEncerrada(Aula aula) {
        if (!Boolean.TRUE.equals(aula.getMarcada()) || aula.getTurma() == null) {
            return false;
        }

        Turma turma = aula.getTurma();
        LocalTime horaInicio = turma.getHoraInicio();
        int duracao = turma.getDuracaoMinutos() != null ? turma.getDuracaoMinutos() : 50;
        LocalDateTime fimDaAula = LocalDateTime.of(aula.getDataAula(), horaInicio).plusMinutes(duracao);

        return !fimDaAula.isAfter(LocalDateTime.now());
    }

    public DashboardAlertaResponseDto obterAlertas() {
        LocalDate hoje = hoje();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = inicioMes.plusMonths(1).minusDays(1);

        long alunosParaReagendamento = aulaAlunoRepository.countAguardandoReagendamento();
        long aulasCanceladasMes = aulaAlunoRepository.countFaltasNoPeriodo(inicioMes, fimMes);
        long aulasRemarcadasMes = aulaAlunoRepository.countFaltasRemarcadasNoPeriodo(inicioMes, fimMes);
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

        // "Realizadas de X programadas" é por aluno, igual canceladas/remarcadas abaixo: conta
        // AulaAluno com status REALIZADA, de todos os agendamentos de aluno no mês. Antes contava
        // sessoes de turma (Aula.marcada), uma granularidade diferente da usada no resto do card.
        long aulasRealizadas = aulaAlunoRepository.countByStatusAndAula_DataAulaBetween("REALIZADA", inicioMes, fimMes);
        long aulasProgramadas = aulaAlunoRepository.countByAula_DataAulaBetween(inicioMes, fimMes);

        // "Canceladas"/"Remarcadas" são sobre faltas de alunos individuais, não sobre a sessão
        // inteira — por isso usam a mesma base da falta (status AUSENTE / reposição vinculada).
        long aulasCanceladas = aulaAlunoRepository.countFaltasNoPeriodo(inicioMes, fimMes);
        long aulasRemarcadas = aulaAlunoRepository.countFaltasRemarcadasNoPeriodo(inicioMes, fimMes);

        double taxaRemarcacao = aulasCanceladas == 0 ? 0.0 : (double) aulasRemarcadas / aulasCanceladas;

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

        List<DesempenhoMensalResponseDto> resultado = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(hoje.minusMonths(i));
            LocalDate inicio = mes.atDay(1);
            LocalDate fim = mes.atEndOfMonth();

            long realizadas = aulaAlunoRepository.countByStatusAndAula_DataAulaBetween("REALIZADA", inicio, fim);
            long canceladas = aulaAlunoRepository.countFaltasNoPeriodo(inicio, fim);

            resultado.add(new DesempenhoMensalResponseDto(mes.toString(), realizadas, canceladas));
        }
        return resultado;
    }

    private List<DesempenhoMensalResponseDto> obterDesempenhoSemanal() {
        LocalDate inicioSemanaAtual = hoje().with(DayOfWeek.MONDAY);

        List<DesempenhoMensalResponseDto> resultado = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            LocalDate inicio = inicioSemanaAtual.minusWeeks(i);
            LocalDate fim = inicio.plusDays(6);

            long realizadas = aulaAlunoRepository.countByStatusAndAula_DataAulaBetween("REALIZADA", inicio, fim);
            long canceladas = aulaAlunoRepository.countFaltasNoPeriodo(inicio, fim);

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
        Integer turmaId = turma != null ? turma.getId() : null;
        LocalTime horaInicio = turma != null ? turma.getHoraInicio() : null;
        String turmaDiaSemana = turma != null && turma.getDiaSemana() != null ? turma.getDiaSemana().name() : null;
        String professorNome = aula.getProfessor() != null ? aula.getProfessor().getNome() : null;
        long totalAlunos = aula.getAlunos() == null ? 0 : aula.getAlunos().size();

        return new AulaHojeResponseDto(aula.getId(), turmaId, horaInicio, professorNome, turmaDiaSemana, totalAlunos);
    }

    private LocalDate hoje() {
        return LocalDate.now();
    }
}
