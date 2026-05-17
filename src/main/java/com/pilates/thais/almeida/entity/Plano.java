package com.pilates.thais.almeida.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Plano extends Auditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private Integer frequenciaSemanal;
    private Integer validadeDias;
    private Double valorMensal;

    @OneToMany(mappedBy = "plano")
    private List<AlunoPlano> alunoPlanos = new ArrayList<>();

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

    public List<AlunoPlano> getAlunoPlanos() {
        return alunoPlanos;
    }

    public void setAlunoPlanos(List<AlunoPlano> alunoPlanos) {
        this.alunoPlanos = alunoPlanos;
    }
}
