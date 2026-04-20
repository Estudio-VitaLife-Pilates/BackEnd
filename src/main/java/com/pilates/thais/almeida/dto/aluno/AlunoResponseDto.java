package com.pilates.thais.almeida.dto.aluno;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "id","nome","telefone","cpf","email","fichaAnamnese","ativo","dataNascimento","dataCadastro"
})
public class AlunoResponseDto {
    private Integer id;
    private String nome;
    private String telefone;
    private String cpf;
    private String email;
    private boolean ativo;
    private LocalDate dataNascimento;
    private LocalDate dataCadastro;
    private String fichaAnamnese;

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getFichaAnamnese() {
        return fichaAnamnese;
    }

    public void setFichaAnamnese(String fichaAnamnese) {
        this.fichaAnamnese = fichaAnamnese;
    }
}
