package com.pilates.thais.almeida.dto.aula;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "id", "alunoId", "status", "aulaOrigemId", "dataAula"
})
public class AulaAlunoDetailsResponseDto {

    private Integer id;
    private Integer alunoId;
    private String status;
    private Integer aulaOrigemId;
    private LocalDate dataAula;

    public Integer getId() {
        return id;
    }

    public Integer getAlunoId() {
        return alunoId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getAulaOrigemId() {
        return aulaOrigemId;
    }

    public LocalDate getDataAula() {
        return dataAula;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAlunoId(Integer alunoId) {
        this.alunoId = alunoId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAulaOrigemId(Integer aulaOrigemId) {
        this.aulaOrigemId = aulaOrigemId;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }
}