package com.pilates.thais.almeida.notificacao;

import com.pilates.thais.almeida.event.LembreteAulaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SmsCanalNotificacaoTest {

    @Mock
    private SmsGateway smsGateway;

    private SmsCanalNotificacao canal;

    @BeforeEach
    void setUp() {
        canal = new SmsCanalNotificacao(smsGateway, "VitaLife Pilates");
    }

    @Test
    @DisplayName("Deve enviar o SMS para o celular em formato internacional, com texto sem acentos e referencia da aula/aluno")
    void deveEnviarSmsFormatado() {
        canal.enviarLembrete(evento("(11) 99999-0000", "João da Conceição"));

        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        verify(smsGateway).enviar(org.mockito.ArgumentMatchers.eq("+5511999990000"), texto.capture(),
                org.mockito.ArgumentMatchers.eq("aula-10-aluno-1"));
        assertEquals("VitaLife Pilates: Joao, lembrete da sua aula de Pilates: quarta-feira, 14/10 as 18:00.",
                texto.getValue());
    }

    @Test
    @DisplayName("Nao deve usar nenhum caractere acentuado no texto (terca-feira, por exemplo)")
    void naoDeveTerAcentos() {
        LembreteAulaEvent terca = new LembreteAulaEvent(10, 1, "Ângela", "a@a.com", "11999990000",
                LocalDateTime.of(2026, 10, 13, 9, 30), "Ana", "TERCA");

        canal.enviarLembrete(terca);

        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        verify(smsGateway).enviar(anyString(), texto.capture(), anyString());
        assertTrue(texto.getValue().contains("terca-feira, 13/10 as 09:30"), texto.getValue());
        assertTrue(texto.getValue().chars().allMatch(c -> c < 128), "so ASCII: " + texto.getValue());
    }

    @Test
    @DisplayName("Nao deve passar de 160 caracteres, para nao virar dois SMS")
    void naoDevePassarDoLimite() {
        canal.enviarLembrete(evento("11999990000", "A".repeat(300)));

        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        verify(smsGateway).enviar(anyString(), texto.capture(), anyString());
        assertEquals(160, texto.getValue().length());
    }

    @Test
    @DisplayName("Deve usar 'aluno' quando o nome nao vem no evento")
    void deveUsarAlunoQuandoSemNome() {
        canal.enviarLembrete(evento("11999990000", null));

        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        verify(smsGateway).enviar(anyString(), texto.capture(), anyString());
        assertTrue(texto.getValue().startsWith("VitaLife Pilates: aluno, lembrete"), texto.getValue());
    }

    @Test
    @DisplayName("Nao deve enviar nem lancar erro quando o aluno nao tem celular valido")
    void naoDeveEnviarSemCelularValido() {
        canal.enviarLembrete(evento(null, "Bruno"));
        canal.enviarLembrete(evento("   ", "Bruno"));
        canal.enviarLembrete(evento("1133334444", "Bruno"));

        verifyNoInteractions(smsGateway);
    }

    @Test
    @DisplayName("Deve propagar a falha temporaria do gateway para o consumer tentar de novo")
    void devePropagarFalhaDoGateway() {
        doThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
                .when(smsGateway).enviar(anyString(), anyString(), anyString());

        assertThrows(HttpServerErrorException.class,
                () -> canal.enviarLembrete(evento("11999990000", "Bruno")));
    }

    private LembreteAulaEvent evento(String telefone, String nome) {
        return new LembreteAulaEvent(10, 1, nome, "bruno@email.com", telefone,
                LocalDateTime.of(2026, 10, 14, 18, 0), "Ana Souza", "QUARTA");
    }
}
