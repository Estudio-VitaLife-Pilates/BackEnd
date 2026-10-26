package com.pilates.thais.almeida.producer;

import com.pilates.thais.almeida.event.Evento;

public interface EventoPublicador {
    void publicar(Evento evento);
}
