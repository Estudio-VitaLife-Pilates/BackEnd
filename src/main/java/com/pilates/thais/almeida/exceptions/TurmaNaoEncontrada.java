package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Turma não encontrada")
public class TurmaNaoEncontrada extends RuntimeException {
    public TurmaNaoEncontrada(String message) {
        super(message);
    }
}
