package com.pilates.thais.almeida.dto.aluno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public class AlunoRequestDto extends AlunoDadosDto {
    @NotNull
    @NotBlank
    @Override
    public String getNome() { return super.getNome(); }

    @CPF
    @Override
    public String getCpf() { return super.getCpf(); }

    @Email
    @Override
    public String getEmail() { return super.getEmail(); }

    @Past
    @Override
    public LocalDate getDataNascimento() { return super.getDataNascimento(); }
}
