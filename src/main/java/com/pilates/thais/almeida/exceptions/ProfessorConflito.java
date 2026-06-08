package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Conflito no request")
public class ProfessorConflito extends RuntimeException {
    public ProfessorConflito(String message) {
        super(message);
    }
}
