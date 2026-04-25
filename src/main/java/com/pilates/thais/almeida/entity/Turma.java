package com.pilates.thais.almeida.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana ;
    private LocalTime horaInicio;
    private Integer duracaoMinutos;
    private Integer capacidadeMax;
    private Boolean ativa;
    @OneToMany(mappedBy = "turma")
    private List<AlunoTurma> alunos;

    public List<AlunoTurma> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<AlunoTurma> alunos) {
        this.alunos = alunos;
    }

    public enum DiaSemana {
        SEGUNDA,
        TERCA,
        QUARTA,
        QUINTA,
        SEXTA,
        SABADO,
        DOMINGO
    }

    public Turma() {
    }

    public Turma( DiaSemana diaSemana, LocalTime horaInicio, Integer duracaoMinutos, Integer capacidadeMax, Boolean ativa ) {

        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.duracaoMinutos = duracaoMinutos;
        this.capacidadeMax = capacidadeMax;
        this.ativa = ativa;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Integer getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(Integer capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

}
