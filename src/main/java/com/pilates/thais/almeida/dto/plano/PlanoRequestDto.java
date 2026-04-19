package com.pilates.thais.almeida.dto.plano;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PlanoRequestDto {
    @NotBlank
    @NotNull
    private String nome;

    @Positive
    @NotNull
    private Integer frequenciaSemanal;

    @Positive
    @NotNull
    private Integer validadeDias;

    @Positive
    @NotNull
    private Double valorMensal;

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
