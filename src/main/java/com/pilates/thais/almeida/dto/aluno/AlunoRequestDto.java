package com.pilates.thais.almeida.dto.aluno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public class AlunoRequestDto {
    @NotNull
    @NotBlank
    private String nome;

    private String telefone;

    @CPF
    private String cpf;

    @Email
    private String email;

    @Past
    private LocalDate dataNascimento;

    private String fichaAnamnese;

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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getFichaAnamnese() {
        return fichaAnamnese;
    }

    public void setFichaAnamnese(String fichaAnamnese) {
        this.fichaAnamnese = fichaAnamnese;
    }
}
