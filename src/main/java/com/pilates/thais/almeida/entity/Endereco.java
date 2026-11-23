package com.pilates.thais.almeida.entity;

import jakarta.persistence.*;

@Entity
public class Endereco extends EnderecoDados {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "aluno_id", unique = true)
    private Aluno aluno;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
}
