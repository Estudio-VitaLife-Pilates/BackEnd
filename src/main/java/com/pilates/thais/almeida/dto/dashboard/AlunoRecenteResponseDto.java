package com.pilates.thais.almeida.dto.dashboard;

import java.time.LocalDate;

public class AlunoRecenteResponseDto {
    private Integer id;
    private String nome;
    private String telefone;
    private String nomePlano;
    private Integer frequenciaSemanal;
    private LocalDate dataCadastro;

    public AlunoRecenteResponseDto() {
    }

    public AlunoRecenteResponseDto(Integer id, String nome, String telefone, String nomePlano,
                                   Integer frequenciaSemanal, LocalDate dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.nomePlano = nomePlano;
        this.frequenciaSemanal = frequenciaSemanal;
        this.dataCadastro = dataCadastro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNomePlano() {
        return nomePlano;
    }

    public void setNomePlano(String nomePlano) {
        this.nomePlano = nomePlano;
    }

    public Integer getFrequenciaSemanal() {
        return frequenciaSemanal;
    }

    public void setFrequenciaSemanal(Integer frequenciaSemanal) {
        this.frequenciaSemanal = frequenciaSemanal;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}