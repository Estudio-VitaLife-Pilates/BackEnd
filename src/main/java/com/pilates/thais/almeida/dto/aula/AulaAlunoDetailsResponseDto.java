package com.pilates.thais.almeida.dto.aula;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "id", "alunoId", "alunoNome", "alunoTelefone", "status", "aulaId", "aulaOrigemId", "reposicao", "dataAula"
})
public class AulaAlunoDetailsResponseDto {

    private Integer id;
    private Integer alunoId;
    private String status;
    private Integer aulaOrigemId;
    private LocalDate dataAula;
    private String alunoNome;
    private String alunoTelefone;
    private Integer aulaId;
    private Boolean reposicao;

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public String getAlunoTelefone() {
        return alunoTelefone;
    }

    public void setAlunoTelefone(String alunoTelefone) {
        this.alunoTelefone = alunoTelefone;
    }

    public Integer getAulaId() {
        return aulaId;
    }

    public void setAulaId(Integer aulaId) {
        this.aulaId = aulaId;
    }

    public Boolean getReposicao() {
        return reposicao;
    }

    public void setReposicao(Boolean reposicao) {
        this.reposicao = reposicao;
    }


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