package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Aluno não encontrado")
public class AlunoNaoEncontrado extends RuntimeException{
    public AlunoNaoEncontrado(String message) {
        super(message);
    }
}
