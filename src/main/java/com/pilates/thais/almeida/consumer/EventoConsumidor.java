package com.pilates.thais.almeida.consumer;

import com.pilates.thais.almeida.event.Evento;

public interface EventoConsumidor<E extends Evento> {
    void processar(E evento);
}
