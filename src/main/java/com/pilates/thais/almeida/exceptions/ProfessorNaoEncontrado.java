package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Professor não encontrado")
public class ProfessorNaoEncontrado extends RuntimeException {
    public ProfessorNaoEncontrado(String message) {
        super(message);
    }
}
