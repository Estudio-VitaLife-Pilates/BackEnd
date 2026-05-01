package com.pilates.thais.almeida.dto.plano;

public class PlanoResponseDto {
    private Integer id;
    private String nome;
    private Integer frequenciaSemanal;
    private Integer validadeDias;
    private Double valorMensal;

    public PlanoResponseDto(Integer id, String nome, Integer frequenciaSemanal, Integer validadeDias, Double valorMensal) {
        this.id = id;
        this.nome = nome;
        this.frequenciaSemanal = frequenciaSemanal;
        this.validadeDias = validadeDias;
        this.valorMensal = valorMensal;
    }

    public PlanoResponseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getFrequenciaSemanal() {
        return frequenciaSemanal;
    }

    public void setFrequenciaSemanal(Integer frequenciaSemanal) {
        this.frequenciaSemanal = frequenciaSemanal;
    }

    public Integer getValidadeDias() {
        return validadeDias;
    }

    public void setValidadeDias(Integer validadeDias) {
        this.validadeDias = validadeDias;
    }

    public Double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(Double valorMensal) {
        this.valorMensal = valorMensal;
    }
}
