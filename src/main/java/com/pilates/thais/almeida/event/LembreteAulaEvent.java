package com.pilates.thais.almeida.event;

import java.time.LocalDateTime;

public class LembreteAulaEvent implements Evento {

    public static final String ROUTING_KEY = "evento.aula.lembrete";

    private Integer aulaId;
    private Integer alunoId;
    private String nomeAluno;
    private String emailAluno;
    private String telefoneAluno;
    private LocalDateTime dataHoraAula;
    private String nomeProfessor;
    private String turmaDiaSemana;

    public LembreteAulaEvent() {
    }

    public LembreteAulaEvent(Integer aulaId, Integer alunoId, String nomeAluno, String emailAluno,
                              String telefoneAluno, LocalDateTime dataHoraAula, String nomeProfessor,
                              String turmaDiaSemana) {
        this.aulaId = aulaId;
        this.alunoId = alunoId;
        this.nomeAluno = nomeAluno;
        this.emailAluno = emailAluno;
        this.telefoneAluno = telefoneAluno;
        this.dataHoraAula = dataHoraAula;
        this.nomeProfessor = nomeProfessor;
        this.turmaDiaSemana = turmaDiaSemana;
    }

    @Override
    public String routingKey() {
        return ROUTING_KEY;
    }

    public Integer getAulaId() {
        return aulaId;
    }

    public void setAulaId(Integer aulaId) {
        this.aulaId = aulaId;
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

    public String getTelefoneAluno() {
        return telefoneAluno;
    }

    public void setTelefoneAluno(String telefoneAluno) {
        this.telefoneAluno = telefoneAluno;
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
