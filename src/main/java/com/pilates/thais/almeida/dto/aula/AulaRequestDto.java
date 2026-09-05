package com.pilates.thais.almeida.dto.aula;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AulaRequestDto {

    @NotNull
    private Integer turmaId;

    @NotNull
    private Integer professorId;

    @NotNull
    private LocalDate dataAula;

    private Boolean marcada = true;

    public AulaRequestDto(Integer turmaId, Integer professorId, LocalDate dataAula, Boolean marcada) {
        this.turmaId = turmaId;
        this.professorId = professorId;
        this.dataAula = dataAula;
        this.marcada = marcada;
    }

    public static class ReposicaoRequestDto {
        private Integer alunoId;
        private Integer aulaOrigemId;

        public Integer getAlunoId() {
            return alunoId;
        }

        public void setAlunoId(Integer alunoId) {
            this.alunoId = alunoId;
        }

        public Integer getAulaOrigemId() {
            return aulaOrigemId;
        }

        public void setAulaOrigemId(Integer aulaOrigemId) {
            this.aulaOrigemId = aulaOrigemId;
        }
    }

    public AulaRequestDto() {
    }

    public Integer getTurmaId() {
        return turmaId;
    }

    public Integer getProfessorId() {
        return professorId;
    }

    public LocalDate getDataAula() {
        return dataAula;
    }

    public Boolean getMarcada() {
        return marcada;
    }

    public void setTurmaId(Integer turmaId) {
        this.turmaId = turmaId;
    }

    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }

    public void setMarcada(Boolean marcada) {
        this.marcada = marcada;
    }
}
