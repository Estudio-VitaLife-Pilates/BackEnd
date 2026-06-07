package com.pilates.thais.almeida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Usuário já cadastrado no máximo de turmas que o plano permite.")
public class UsuarioJaCadastradoNoMaximoDeTurmas extends RuntimeException {
    public UsuarioJaCadastradoNoMaximoDeTurmas(String message) {
        super(message);
    }
}
