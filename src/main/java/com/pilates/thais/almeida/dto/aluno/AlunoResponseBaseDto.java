package com.pilates.thais.almeida.dto.aluno;

import java.time.LocalDate;

public class AlunoResponseBaseDto extends AlunoDadosDto {
    private Integer id;
    private Boolean ativo;
    private LocalDate dataCadastro;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
