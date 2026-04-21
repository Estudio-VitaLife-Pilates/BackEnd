package com.pilates.thais.almeida.dto.turma;

import com.pilates.thais.almeida.dto.professor.ProfessorReponseDto;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalTime;

public class TurmaResponseDto {
    private Integer id;
    private Turma.DiaSemana diaSemana ;
    private LocalTime horaInicio;
    private Integer duracaoMinutos;
    private Integer capacidadeMax;
    private Boolean ativa;



    public enum DiaSemana {
        SEGUNDA,
        TERCA,
        QUARTA,
        QUINTA,
        SEXTA,
        SABADO,
        DOMINGO
    }

    public TurmaResponseDto(Integer id, Turma.DiaSemana diaSemana, LocalTime horaInicio, Integer duracaoMinutos, Boolean ativa, Integer capacidadeMax) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.duracaoMinutos = duracaoMinutos;
        this.ativa = ativa;
        this.capacidadeMax = capacidadeMax;

    }

    public TurmaResponseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }



    public Turma.DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Turma.DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(Integer capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }
}
