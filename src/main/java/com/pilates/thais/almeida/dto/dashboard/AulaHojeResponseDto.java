package com.pilates.thais.almeida.dto.dashboard;

import java.time.LocalTime;

public class AulaHojeResponseDto {
    private Integer id;
    private LocalTime horaInicio;
    private String professorNome;
    private String turmaDiaSemana;
    private Long totalAlunos;

    public AulaHojeResponseDto() {
    }

    public AulaHojeResponseDto(Integer id, LocalTime horaInicio, String professorNome,
                               String turmaDiaSemana, Long totalAlunos) {
        this.id = id;
        this.horaInicio = horaInicio;
        this.professorNome = professorNome;
        this.turmaDiaSemana = turmaDiaSemana;
        this.totalAlunos = totalAlunos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getProfessorNome() {
        return professorNome;
    }

    public void setProfessorNome(String professorNome) {
        this.professorNome = professorNome;
    }

    public String getTurmaDiaSemana() {
        return turmaDiaSemana;
    }

    public void setTurmaDiaSemana(String turmaDiaSemana) {
        this.turmaDiaSemana = turmaDiaSemana;
    }

    public Long getTotalAlunos() {
        return totalAlunos;
    }

    public void setTotalAlunos(Long totalAlunos) {
        this.totalAlunos = totalAlunos;
    }
}