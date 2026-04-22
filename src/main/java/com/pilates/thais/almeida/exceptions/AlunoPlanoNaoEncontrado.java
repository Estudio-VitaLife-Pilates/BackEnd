package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "AlunoPlano não encontrado")
public class AlunoPlanoNaoEncontrado extends RuntimeException {
    public AlunoPlanoNaoEncontrado(String message) {
        super(message);
    }
}
