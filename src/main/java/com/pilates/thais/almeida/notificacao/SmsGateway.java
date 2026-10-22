package com.pilates.thais.almeida.notificacao;

public interface SmsGateway {

    void enviar(String telefone, String texto, String referencia);
}
