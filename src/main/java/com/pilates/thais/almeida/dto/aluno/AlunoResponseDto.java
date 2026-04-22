package com.pilates.thais.almeida.dto.aluno;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
        "id","nome","telefone","cpf","email","fichaAnamnese","ativo","dataNascimento","dataCadastro", "planos"
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

    private List<AlunoPlanoResponseDto> planos;

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

    public List<AlunoPlanoResponseDto> getPlanos() {
        return planos;
    }

    public void setPlanos(List<AlunoPlanoResponseDto> planos) {
        this.planos = planos;
    }

    public static class AlunoPlanoResponseDto{
        private Integer idPlano;
        private String nome;
        private Integer frequenciaSemanal;
        private Integer validadeDias;
        private Double valorMensal;

        private Integer idAlunoPlano;
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private Boolean ativo;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Integer getFrequenciaSemanal() {
            return frequenciaSemanal;
        }

        public void setFrequenciaSemanal(Integer frequenciaSemanal) {
            this.frequenciaSemanal = frequenciaSemanal;
        }

        public Integer getValidadeDias() {
            return validadeDias;
        }

        public void setValidadeDias(Integer validadeDias) {
            this.validadeDias = validadeDias;
        }

        public Double getValorMensal() {
            return valorMensal;
        }

        public void setValorMensal(Double valorMensal) {
            this.valorMensal = valorMensal;
        }

        public Integer getIdPlano() {
            return idPlano;
        }

        public void setIdPlano(Integer idPlano) {
            this.idPlano = idPlano;
        }

        public Integer getIdAlunoPlano() {
            return idAlunoPlano;
        }

        public void setIdAlunoPlano(Integer idAlunoPlano) {
            this.idAlunoPlano = idAlunoPlano;
        }

        public LocalDate getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(LocalDate dataInicio) {
            this.dataInicio = dataInicio;
        }

        public LocalDate getDataFim() {
            return dataFim;
        }

        public void setDataFim(LocalDate dataFim) {
            this.dataFim = dataFim;
        }

        public Boolean getAtivo() {
            return ativo;
        }

        public void setAtivo(Boolean ativo) {
            this.ativo = ativo;
        }
    }
}
