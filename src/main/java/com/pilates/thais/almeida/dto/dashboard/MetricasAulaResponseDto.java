package com.pilates.thais.almeida.dto.dashboard;

public class MetricasAulaResponseDto {
    private Long aulasCanceladas;
    private Long aulasRemarcadas;
    private Double taxaRemarcacao;
    private Long aulasRealizadas;
    private Long aulasProgramadas;

    public MetricasAulaResponseDto() {
    }

    public MetricasAulaResponseDto(Long aulasCanceladas, Long aulasRemarcadas, Double taxaRemarcacao,
                                   Long aulasRealizadas, Long aulasProgramadas) {
        this.aulasCanceladas = aulasCanceladas;
        this.aulasRemarcadas = aulasRemarcadas;
        this.taxaRemarcacao = taxaRemarcacao;
        this.aulasRealizadas = aulasRealizadas;
        this.aulasProgramadas = aulasProgramadas;
    }

    public Long getAulasCanceladas() {
        return aulasCanceladas;
    }

    public void setAulasCanceladas(Long aulasCanceladas) {
        this.aulasCanceladas = aulasCanceladas;
    }

    public Long getAulasRemarcadas() {
        return aulasRemarcadas;
    }

    public void setAulasRemarcadas(Long aulasRemarcadas) {
        this.aulasRemarcadas = aulasRemarcadas;
    }

    public Double getTaxaRemarcacao() {
        return taxaRemarcacao;
    }

    public void setTaxaRemarcacao(Double taxaRemarcacao) {
        this.taxaRemarcacao = taxaRemarcacao;
    }

    public Long getAulasRealizadas() {
        return aulasRealizadas;
    }

    public void setAulasRealizadas(Long aulasRealizadas) {
        this.aulasRealizadas = aulasRealizadas;
    }

    public Long getAulasProgramadas() {
        return aulasProgramadas;
    }

    public void setAulasProgramadas(Long aulasProgramadas) {
        this.aulasProgramadas = aulasProgramadas;
    }
}