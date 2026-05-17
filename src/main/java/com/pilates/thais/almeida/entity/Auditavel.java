package com.pilates.thais.almeida.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
    @EntityListeners(AuditingEntityListener.class)
    public abstract class Auditavel {

        @CreatedBy
        @Column(name = "criado_por_usuario_id", updatable = false)
        private Integer criadoPor;

        @LastModifiedBy
        @Column(name = "atualizado_por_usuario_id")
        private Integer atualizadoPor;

        @CreatedDate
        @Column(name = "data_criacao", updatable = false)
        private LocalDateTime dataCriacao;

        @LastModifiedDate
        @Column(name = "data_atualizacao")
        private LocalDateTime dataAtualizacao;

    public Integer getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(Integer atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public Integer getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Integer criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}

