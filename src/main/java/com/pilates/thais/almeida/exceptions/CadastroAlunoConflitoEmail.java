package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Aluno com email duplicado")

public class CadastroAlunoConflitoEmail extends RuntimeException {
    public CadastroAlunoConflitoEmail(String message) {
        super(message);
    }
}
