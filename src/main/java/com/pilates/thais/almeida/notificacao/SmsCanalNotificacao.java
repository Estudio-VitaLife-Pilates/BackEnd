package com.pilates.thais.almeida.notificacao;

import com.pilates.thais.almeida.event.LembreteAulaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "lembrete.canal", havingValue = "sms")
public class SmsCanalNotificacao implements CanalNotificacao {

    private static final Logger log = LoggerFactory.getLogger(SmsCanalNotificacao.class);

    // limite de caracteres por conta do smsgo
    private static final int LIMITE_CARACTERES = 160;

    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("EEEE, dd/MM", PT_BR);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm", PT_BR);

    private final SmsGateway smsGateway;
    private final String nomeEstudio;

    public SmsCanalNotificacao(SmsGateway smsGateway,
                               @Value("${lembrete.sms.nome-estudio}") String nomeEstudio) {
        this.smsGateway = smsGateway;
        this.nomeEstudio = nomeEstudio;
    }

    @Override
    public void enviarLembrete(LembreteAulaEvent evento) {
        Optional<String> telefone = TelefoneBrasil.paraInternacional(evento.getTelefoneAluno());
        if (telefone.isEmpty()) {
            // sem celular valido nao ha o que reenviar: nao lanca excecao para nao gastar as tentativas do consumer
            log.warn("Aluno {} sem celular valido cadastrado; lembrete da aula {} nao enviado por SMS",
                    evento.getAlunoId(), evento.getAulaId());
            return;
        }

        smsGateway.enviar(telefone.get(), montarTexto(evento),
                "aula-" + evento.getAulaId() + "-aluno-" + evento.getAlunoId());
        log.info("Lembrete de aula enviado por SMS: aluno {}, aula {}", evento.getAlunoId(), evento.getAulaId());
    }

    // parte dos acentos
    private String montarTexto(LembreteAulaEvent evento) {
        String texto = nomeEstudio + ": " + primeiroNome(evento.getNomeAluno())
                + ", lembrete da sua aula de Pilates: "
                + evento.getDataHoraAula().format(DIA) + " as " + evento.getDataHoraAula().format(HORA) + ".";
        texto = semAcentos(texto);
        return texto.length() > LIMITE_CARACTERES ? texto.substring(0, LIMITE_CARACTERES) : texto;
    }

    private String primeiroNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "aluno";
        }
        return nome.trim().split("\\s+")[0];
    }

    private String semAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
