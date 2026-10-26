package com.pilates.thais.almeida.notificacao;

import com.pilates.thais.almeida.event.LembreteAulaEvent;

public interface CanalNotificacao {
    void enviarLembrete(LembreteAulaEvent evento);
}
