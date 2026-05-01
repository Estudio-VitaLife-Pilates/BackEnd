package com.pilates.thais.almeida.dto.aula;

import java.time.LocalDate;

public class AulaResponseDto {
    private Integer id;
    private LocalDate dataAula;
    private Boolean marcada;

    public AulaResponseDto(Integer id, LocalDate dataAula, Boolean marcada) {
        this.id = id;
        this.dataAula = dataAula;
        this.marcada = marcada;
    }

    public AulaResponseDto() {
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }

    public void setMarcada(Boolean marcada) {
        this.marcada = marcada;
    }
}