package com.pilates.thais.almeida.dto.dashboard;

public class DashboardKpisResponseDto {
    private Long totalAlunos;
    private Long novosAlunosMes;
    private Double taxaOcupacao;
    private Double variacaoOcupacaoMes;
    private Long aulasSemana;
    private Long aulasCanceladasSemana;
    private Long aulasProgramadasSemana;
    private Long turmasRealizadasSemana;
    private Long turmasAgendadasSemana;
    private Double taxaRetencao;
    private Double taxaSaidaMes;

    public DashboardKpisResponseDto() {
    }

    public DashboardKpisResponseDto(Long totalAlunos, Long novosAlunosMes, Double taxaOcupacao,
                                   Double variacaoOcupacaoMes, Long aulasSemana, Long aulasCanceladasSemana,
                                   Long aulasProgramadasSemana, Long turmasRealizadasSemana,
                                   Long turmasAgendadasSemana, Double taxaRetencao, Double taxaSaidaMes) {
        this.totalAlunos = totalAlunos;
        this.novosAlunosMes = novosAlunosMes;
        this.taxaOcupacao = taxaOcupacao;
        this.variacaoOcupacaoMes = variacaoOcupacaoMes;
        this.aulasSemana = aulasSemana;
        this.aulasCanceladasSemana = aulasCanceladasSemana;
        this.aulasProgramadasSemana = aulasProgramadasSemana;
        this.turmasRealizadasSemana = turmasRealizadasSemana;
        this.turmasAgendadasSemana = turmasAgendadasSemana;
        this.taxaRetencao = taxaRetencao;
        this.taxaSaidaMes = taxaSaidaMes;
    }

    public Long getTotalAlunos() {
        return totalAlunos;
    }

    public void setTotalAlunos(Long totalAlunos) {
        this.totalAlunos = totalAlunos;
    }

    public Long getNovosAlunosMes() {
        return novosAlunosMes;
    }

    public void setNovosAlunosMes(Long novosAlunosMes) {
        this.novosAlunosMes = novosAlunosMes;
    }

    public Double getTaxaOcupacao() {
        return taxaOcupacao;
    }

    public void setTaxaOcupacao(Double taxaOcupacao) {
        this.taxaOcupacao = taxaOcupacao;
    }

    public Double getVariacaoOcupacaoMes() {
        return variacaoOcupacaoMes;
    }

    public void setVariacaoOcupacaoMes(Double variacaoOcupacaoMes) {
        this.variacaoOcupacaoMes = variacaoOcupacaoMes;
    }

    public Long getAulasSemana() {
        return aulasSemana;
    }

    public void setAulasSemana(Long aulasSemana) {
        this.aulasSemana = aulasSemana;
    }

    public Long getAulasCanceladasSemana() {
        return aulasCanceladasSemana;
    }

    public void setAulasCanceladasSemana(Long aulasCanceladasSemana) {
        this.aulasCanceladasSemana = aulasCanceladasSemana;
    }

    public Long getAulasProgramadasSemana() {
        return aulasProgramadasSemana;
    }

    public void setAulasProgramadasSemana(Long aulasProgramadasSemana) {
        this.aulasProgramadasSemana = aulasProgramadasSemana;
    }

    public Long getTurmasRealizadasSemana() {
        return turmasRealizadasSemana;
    }

    public void setTurmasRealizadasSemana(Long turmasRealizadasSemana) {
        this.turmasRealizadasSemana = turmasRealizadasSemana;
    }

    public Long getTurmasAgendadasSemana() {
        return turmasAgendadasSemana;
    }

    public void setTurmasAgendadasSemana(Long turmasAgendadasSemana) {
        this.turmasAgendadasSemana = turmasAgendadasSemana;
    }

    public Double getTaxaRetencao() {
        return taxaRetencao;
    }

    public void setTaxaRetencao(Double taxaRetencao) {
        this.taxaRetencao = taxaRetencao;
    }

    public Double getTaxaSaidaMes() {
        return taxaSaidaMes;
    }

    public void setTaxaSaidaMes(Double taxaSaidaMes) {
        this.taxaSaidaMes = taxaSaidaMes;
    }
}