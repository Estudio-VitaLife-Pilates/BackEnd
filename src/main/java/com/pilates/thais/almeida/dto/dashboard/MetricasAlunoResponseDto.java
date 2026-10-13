package com.pilates.thais.almeida.dto.dashboard;

public class MetricasAlunoResponseDto {
    private Long novosAlunos;
    private Long variacaoNovosAlunos;
    private Long desmatriculados;
    private Long variacaoDesmatriculados;
    private Long paraReagendamento;
    private Double taxaSaida;

    public MetricasAlunoResponseDto() {
    }

    public MetricasAlunoResponseDto(Long novosAlunos, Long variacaoNovosAlunos, Long desmatriculados,
                                    Long variacaoDesmatriculados, Long paraReagendamento, Double taxaSaida) {
        this.novosAlunos = novosAlunos;
        this.variacaoNovosAlunos = variacaoNovosAlunos;
        this.desmatriculados = desmatriculados;
        this.variacaoDesmatriculados = variacaoDesmatriculados;
        this.paraReagendamento = paraReagendamento;
        this.taxaSaida = taxaSaida;
    }

    public Long getNovosAlunos() {
        return novosAlunos;
    }

    public void setNovosAlunos(Long novosAlunos) {
        this.novosAlunos = novosAlunos;
    }

    public Long getVariacaoNovosAlunos() {
        return variacaoNovosAlunos;
    }

    public void setVariacaoNovosAlunos(Long variacaoNovosAlunos) {
        this.variacaoNovosAlunos = variacaoNovosAlunos;
    }

    public Long getDesmatriculados() {
        return desmatriculados;
    }

    public void setDesmatriculados(Long desmatriculados) {
        this.desmatriculados = desmatriculados;
    }

    public Long getVariacaoDesmatriculados() {
        return variacaoDesmatriculados;
    }

    public void setVariacaoDesmatriculados(Long variacaoDesmatriculados) {
        this.variacaoDesmatriculados = variacaoDesmatriculados;
    }

    public Long getParaReagendamento() {
        return paraReagendamento;
    }

    public void setParaReagendamento(Long paraReagendamento) {
        this.paraReagendamento = paraReagendamento;
    }

    public Double getTaxaSaida() {
        return taxaSaida;
    }

    public void setTaxaSaida(Double taxaSaida) {
        this.taxaSaida = taxaSaida;
    }
}