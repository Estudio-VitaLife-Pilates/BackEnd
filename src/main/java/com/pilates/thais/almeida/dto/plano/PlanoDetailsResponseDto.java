package com.pilates.thais.almeida.dto.plano;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({
        "id","nome","frequenciaSemanal","validadeDias","valorMensal","alunos"
})
public class PlanoDetailsResponseDto {
    private Integer id;
    private String nome;
    private Integer frequenciaSemanal;
    private Integer validadeDias;
    private Double valorMensal;

    private List<PlanoAlunoResponseDto> alunos = new ArrayList<>();

    public static class PlanoAlunoResponseDto{
        private Integer idAluno;
        private String nome;
        private String telefone;
        private String cpf;
        private String email;
        private Boolean alunoAtivo;
        private LocalDate dataNascimentoAluno;
        private LocalDate dataCadastroAluno;
        private String fichaAnamnese;

        private Integer idPlanoAluno;
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private Boolean planoAtivo;

        public Integer getIdAluno() {
            return idAluno;
        }

        public void setIdAluno(Integer idAluno) {
            this.idAluno = idAluno;
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

        public boolean getAlunoAtivo() {
            return alunoAtivo;
        }

        public void setAlunoAtivo(boolean alunoAtivo) {
            this.alunoAtivo = alunoAtivo;
        }

        public LocalDate getDataNascimentoAluno() {
            return dataNascimentoAluno;
        }

        public void setDataNascimentoAluno(LocalDate dataNascimentoAluno) {
            this.dataNascimentoAluno = dataNascimentoAluno;
        }

        public LocalDate getDataCadastroAluno() {
            return dataCadastroAluno;
        }

        public void setDataCadastroAluno(LocalDate dataCadastroAluno) {
            this.dataCadastroAluno = dataCadastroAluno;
        }

        public String getFichaAnamnese() {
            return fichaAnamnese;
        }

        public void setFichaAnamnese(String fichaAnamnese) {
            this.fichaAnamnese = fichaAnamnese;
        }

        public Integer getIdPlanoAluno() {
            return idPlanoAluno;
        }

        public void setIdPlanoAluno(Integer idPlanoAluno) {
            this.idPlanoAluno = idPlanoAluno;
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

        public Boolean getPlanoAtivo() {
            return planoAtivo;
        }

        public void setPlanoAtivo(Boolean planoAtivo) {
            this.planoAtivo = planoAtivo;
        }
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

    public List<PlanoAlunoResponseDto> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<PlanoAlunoResponseDto> alunos) {
        this.alunos = alunos;
    }
}
