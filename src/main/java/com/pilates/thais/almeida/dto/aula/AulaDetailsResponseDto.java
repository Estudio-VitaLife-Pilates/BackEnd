package com.pilates.thais.almeida.dto.aula;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
        "id", "turma", "professor", "dataAula", "marcada", "alunos"
})
public class AulaDetailsResponseDto {

    private Integer id;
    private String turma;
    private String professor;
    private LocalDate dataAula;
    private Boolean marcada;
    private List<AulaAlunoResponseDto> alunos;

    public Integer getId() {
        return id;
    }

    public String getTurma() {
        return turma;
    }

    public String getProfessor() {
        return professor;
    }

    public LocalDate getDataAula() {
        return dataAula;
    }

    public Boolean getMarcada() {
        return marcada;
    }

    public List<AulaAlunoResponseDto> getAlunos() {
        return alunos;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }

    public void setMarcada(Boolean marcada) {
        this.marcada = marcada;
    }

    public void setAlunos(List<AulaAlunoResponseDto> alunos) {
        this.alunos = alunos;
    }

    public static class AulaAlunoResponseDto {

        private Integer alunoId;
        private String alunoNome;
        private String status;

        public Integer getAlunoId() {
            return alunoId;
        }

        public String getAlunoNome() {
            return alunoNome;
        }

        public String getStatus() {
            return status;
        }

        public void setAlunoId(Integer alunoId) {
            this.alunoId = alunoId;
        }

        public void setAlunoNome(String alunoNome) {
            this.alunoNome = alunoNome;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}