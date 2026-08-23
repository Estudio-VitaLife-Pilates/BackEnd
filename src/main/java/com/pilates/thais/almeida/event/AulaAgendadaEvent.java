package com.pilates.thais.almeida.event;

import java.time.LocalDateTime;

// Evento simples que será enviado quando uma aula for agendada
public class AulaAgendadaEvent {

    private Integer alunoId;
    private String nomeAluno;
    private String emailAluno;
    private LocalDateTime dataAula;
    private String nomeProfessor;
    private String nomeTurma;

    // Construtor vazio (necessário para o RabbitMQ desserializar)
    public AulaAgendadaEvent() {}

    public AulaAgendadaEvent(Integer alunoId, String nomeAluno, String emailAluno,
                            LocalDateTime dataAula, String nomeProfessor, String nomeTurma) {
        this.alunoId = alunoId;
        this.nomeAluno = nomeAluno;
        this.emailAluno = emailAluno;
        this.dataAula = dataAula;
        this.nomeProfessor = nomeProfessor;
        this.nomeTurma = nomeTurma;
    }

    // Getters e Setters
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

    public LocalDateTime getDataAula() {
        return dataAula;
    }

    public void setDataAula(LocalDateTime dataAula) {
        this.dataAula = dataAula;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma)   {
        this.nomeTurma = nomeTurma;
    }
}