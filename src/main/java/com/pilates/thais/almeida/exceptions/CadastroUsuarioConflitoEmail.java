package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Usuário já existe")

public class CadastroUsuarioConflitoEmail extends RuntimeException {
    public CadastroUsuarioConflitoEmail(String message) {
        super(message);
    }
}
