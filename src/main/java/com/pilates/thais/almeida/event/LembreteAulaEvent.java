package com.pilates.thais.almeida.event;

import java.time.LocalDateTime;

public class LembreteAulaEvent {

    private Integer alunoId;
    private String nomeAluno;
    private String emailAluno;
    private LocalDateTime dataHoraAula;
    private String nomeProfessor;
    private String turmaDiaSemana;

    public LembreteAulaEvent() {
    }

    public LembreteAulaEvent(Integer alunoId, String nomeAluno, String emailAluno,
                              LocalDateTime dataHoraAula, String nomeProfessor, String turmaDiaSemana) {
        this.alunoId = alunoId;
        this.nomeAluno = nomeAluno;
        this.emailAluno = emailAluno;
        this.dataHoraAula = dataHoraAula;
        this.nomeProfessor = nomeProfessor;
        this.turmaDiaSemana = turmaDiaSemana;
    }

    public Integer getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Integer alunoId) {
        this.alunoId = alunoId;
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    public String getEmailAluno() {
        return emailAluno;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public LocalDateTime getDataHoraAula() {
        return dataHoraAula;
    }

    public void setDataHoraAula(LocalDateTime dataHoraAula) {
        this.dataHoraAula = dataHoraAula;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public String getTurmaDiaSemana() {
        return turmaDiaSemana;
    }

    public void setTurmaDiaSemana(String turmaDiaSemana) {
        this.turmaDiaSemana = turmaDiaSemana;
    }
}
