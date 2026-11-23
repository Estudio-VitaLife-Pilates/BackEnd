package com.pilates.thais.almeida.dto.aluno;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
        "id","nome","telefone","cpf","email","fichaAnamnese","ativo","dataNascimento","dataCadastro","endereco","planos"
})
public class AlunoDetailsResponseDto extends AlunoResponseBaseDto {
    private List<AlunoPlanoResponseDto> planos;

    public List<AlunoPlanoResponseDto> getPlanos() { return planos; }
    public void setPlanos(List<AlunoPlanoResponseDto> planos) { this.planos = planos; }

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

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public Integer getFrequenciaSemanal() { return frequenciaSemanal; }
        public void setFrequenciaSemanal(Integer frequenciaSemanal) { this.frequenciaSemanal = frequenciaSemanal; }
        public Integer getValidadeDias() { return validadeDias; }
        public void setValidadeDias(Integer validadeDias) { this.validadeDias = validadeDias; }
        public Double getValorMensal() { return valorMensal; }
        public void setValorMensal(Double valorMensal) { this.valorMensal = valorMensal; }
        public Integer getIdPlano() { return idPlano; }
        public void setIdPlano(Integer idPlano) { this.idPlano = idPlano; }
        public Integer getIdAlunoPlano() { return idAlunoPlano; }
        public void setIdAlunoPlano(Integer idAlunoPlano) { this.idAlunoPlano = idAlunoPlano; }
        public LocalDate getDataInicio() { return dataInicio; }
        public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
        public LocalDate getDataFim() { return dataFim; }
        public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
        public Boolean getAtivo() { return ativo; }
        public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    }
}
