package com.pilates.thais.almeida.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Aula extends Auditavel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate dataAula;
    private Boolean marcada = true;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @OneToMany(mappedBy = "aula")
    private List<AulaAluno> alunos = new ArrayList<>();

    public Aula(Integer id, LocalDate dataAula, Boolean marcada, Turma turma, Professor professor, List<AulaAluno> alunos) {
        this.id = id;
        this.dataAula = dataAula;
        this.marcada = marcada;
        this.turma = turma;
        this.professor = professor;
        this.alunos = alunos;
    }

    public Aula() {
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDataAula() {
        return dataAula;
    }

    public Boolean getMarcada() {
        return marcada;
    }

    public Turma getTurma() {
        return turma;
    }

    public Professor getProfessor() {
        return professor;
    }

    public List<AulaAluno> getAlunos() {
        return alunos;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }

    public void setMarcada(Boolean marcada) {
        this.marcada = marcada;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public void setAlunos(List<AulaAluno> alunos) {
        this.alunos = alunos;
    }
}
