package com.pilates.thais.almeida.dto.dashboard;

public class DesempenhoMensalResponseDto {
    private String periodo;
    private Long aulasRealizadas;
    private Long aulasCanceladas;

    public DesempenhoMensalResponseDto() {
    }

    public DesempenhoMensalResponseDto(String periodo, Long aulasRealizadas, Long aulasCanceladas) {
        this.periodo = periodo;
        this.aulasRealizadas = aulasRealizadas;
        this.aulasCanceladas = aulasCanceladas;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Long getAulasRealizadas() {
        return aulasRealizadas;
    }

    public void setAulasRealizadas(Long aulasRealizadas) {
        this.aulasRealizadas = aulasRealizadas;
    }

    public Long getAulasCanceladas() {
        return aulasCanceladas;
    }

    public void setAulasCanceladas(Long aulasCanceladas) {
        this.aulasCanceladas = aulasCanceladas;
    }
}