package com.pilates.thais.almeida.config;

import com.pilates.thais.almeida.event.LembreteAulaEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMqConfigTest {

    private final MessageConverter conversor = new RabbitMqConfig().jsonMessageConverter();

    @Test
    @DisplayName("Deve serializar o evento em JSON com data ISO e desserializar de volta igual")
    void deveSerializarEDesserializarEvento() {
        LembreteAulaEvent original = new LembreteAulaEvent(100, 7, "Maria", "maria@email.com", "11999990000",
                LocalDateTime.of(2026, 10, 9, 9, 30), "Thais", "SEXTA");

        Message mensagem = conversor.toMessage(original, new MessageProperties());
        String json = new String(mensagem.getBody(), StandardCharsets.UTF_8);

        assertTrue(json.contains("\"dataHoraAula\":\"2026-10-09T09:30:00\""), json);

        LembreteAulaEvent recebido = (LembreteAulaEvent) conversor.fromMessage(mensagem);
        assertEquals(original.getAulaId(), recebido.getAulaId());
        assertEquals(original.getAlunoId(), recebido.getAlunoId());
        assertEquals(original.getNomeAluno(), recebido.getNomeAluno());
        assertEquals(original.getEmailAluno(), recebido.getEmailAluno());
        assertEquals(original.getTelefoneAluno(), recebido.getTelefoneAluno());
        assertEquals(original.getDataHoraAula(), recebido.getDataHoraAula());
        assertEquals(original.getNomeProfessor(), recebido.getNomeProfessor());
        assertEquals(original.getTurmaDiaSemana(), recebido.getTurmaDiaSemana());
    }
}
