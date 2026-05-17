package com.pilates.thais.almeida.entity;

import jakarta.persistence.*;

@Entity
public class AulaAluno extends Auditavel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String status = "AGENDADO";

    @ManyToOne
    @JoinColumn(name = "aula_id")
    private Aula aula;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "aula_origem_id")
    private Aula aulaOrigem;

    public AulaAluno(Integer id, String status, Aula aula, Aluno aluno, Aula aulaOrigem) {
        this.id = id;
        this.status = status;
        this.aula = aula;
        this.aluno = aluno;
        this.aulaOrigem = aulaOrigem;
    }

    public AulaAluno() {
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Aula getAula() {
        return aula;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Aula getAulaOrigem() {
        return aulaOrigem;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public void setAulaOrigem(Aula aulaOrigem) {
        this.aulaOrigem = aulaOrigem;
    }
}