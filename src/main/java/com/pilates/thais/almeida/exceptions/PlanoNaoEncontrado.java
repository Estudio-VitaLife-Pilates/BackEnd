package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Recurso não encontrado")
public class PlanoNaoEncontrado extends RuntimeException {
    public PlanoNaoEncontrado(String message) {
        super(message);
    }
}
