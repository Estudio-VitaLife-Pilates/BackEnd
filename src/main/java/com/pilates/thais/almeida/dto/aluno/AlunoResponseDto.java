package com.pilates.thais.almeida.dto.aluno;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "id","nome","telefone","cpf","email","fichaAnamnese","ativo","dataNascimento","dataCadastro","endereco","vencimentoPlanoAtivo"
})
public class AlunoResponseDto extends AlunoResponseBaseDto {
    private LocalDate vencimentoPlanoAtivo;

    public LocalDate getVencimentoPlanoAtivo() { return vencimentoPlanoAtivo; }
    public void setVencimentoPlanoAtivo(LocalDate vencimentoPlanoAtivo) { this.vencimentoPlanoAtivo = vencimentoPlanoAtivo; }
}
