package com.pilates.thais.almeida.notificacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SmsGoGatewayTest {

    private static final String BASE = "https://api.smsgo.test";

    private MockRestServiceServer servidor;
    private SmsGoGateway gateway;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        servidor = MockRestServiceServer.bindTo(builder).build();
        gateway = new SmsGoGateway(builder.build(), "chave-permanente");
    }

    @Test
    @DisplayName("Deve trocar a chave por token e enviar o SMS com phone, message e reference")
    void deveEnviarComTokenBearer() {
        esperarToken("oat_um");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer oat_um"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("\"phone\":\"+5511999990000\""),
                        org.hamcrest.Matchers.containsString("\"message\":\"Oi, tudo bem?\""),
                        org.hamcrest.Matchers.containsString("\"reference\":\"aula-10-aluno-1\""))))
                .andRespond(withSuccess("{\"id\":\"abc\",\"quantity\":1,\"status\":\"queued\"}", MediaType.APPLICATION_JSON));

        gateway.enviar("+5511999990000", "Oi, tudo bem?", "aula-10-aluno-1");

        servidor.verify();
    }

    @Test
    @DisplayName("Deve reaproveitar o token em varios envios, sem pedir outro a cada SMS")
    void deveReaproveitarToken() {
        servidor.expect(ExpectedCount.once(), requestTo(BASE + "/v1/auth/token"))
                .andRespond(withSuccess("{\"token\":\"oat_um\",\"mode\":\"live\"}", MediaType.APPLICATION_JSON));
        servidor.expect(ExpectedCount.times(2), requestTo(BASE + "/v1/sms/send/single"))
                .andExpect(header("Authorization", "Bearer oat_um"))
                .andRespond(withSuccess("{\"id\":\"abc\",\"quantity\":1,\"status\":\"queued\"}", MediaType.APPLICATION_JSON));

        gateway.enviar("+5511999990000", "um", "r1");
        gateway.enviar("+5511999990000", "dois", "r2");

        servidor.verify();
    }

    @Test
    @DisplayName("Deve aceitar a resposta do modo sandbox (test: true)")
    void deveAceitarRespostaDeSandbox() {
        esperarToken("oat_um");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andRespond(withSuccess("{\"id\":\"abc\",\"quantity\":1,\"status\":\"queued\",\"test\":true}",
                        MediaType.APPLICATION_JSON));

        assertDoesNotThrow(() -> gateway.enviar("+5511999990000", "oi", "r1"));
        servidor.verify();
    }

    @Test
    @DisplayName("Deve renovar o token uma vez quando o provedor responde 401 e reenviar")
    void deveRenovarTokenNo401() {
        esperarToken("oat_velho");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andExpect(header("Authorization", "Bearer oat_velho"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":true,\"code\":\"unauthorized\",\"message\":\"Token invalido\"}"));
        esperarToken("oat_novo");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andExpect(header("Authorization", "Bearer oat_novo"))
                .andRespond(withSuccess("{\"id\":\"abc\",\"quantity\":1,\"status\":\"queued\"}", MediaType.APPLICATION_JSON));

        gateway.enviar("+5511999990000", "oi", "r1");

        servidor.verify();
    }

    @Test
    @DisplayName("Nao deve lancar erro nem repetir quando falta saldo (402): repetir so gastaria tentativas")
    void naoDeveRepetirSemSaldo() {
        esperarToken("oat_um");
        servidor.expect(ExpectedCount.once(), requestTo(BASE + "/v1/sms/send/single"))
                .andRespond(withStatus(HttpStatus.PAYMENT_REQUIRED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":true,\"code\":\"insufficient_balance\",\"message\":\"Saldo insuficiente.\"}"));

        assertDoesNotThrow(() -> gateway.enviar("+5511999990000", "oi", "r1"));
        servidor.verify();
    }

    @Test
    @DisplayName("Nao deve lancar erro quando o provedor recusa o numero (422)")
    void naoDeveLancarEm422() {
        esperarToken("oat_um");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":true,\"code\":\"validation_error\",\"message\":\"phone invalido\"}"));

        assertDoesNotThrow(() -> gateway.enviar("+5511999990000", "oi", "r1"));
        servidor.verify();
    }

    @Test
    @DisplayName("Nao deve ficar em loop quando a chave e invalida: tenta renovar uma vez e desiste sem lancar")
    void naoDeveFicarEmLoopComChaveInvalida() {
        servidor.expect(ExpectedCount.times(2), requestTo(BASE + "/v1/auth/token"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":true,\"code\":\"unauthorized\",\"message\":\"chave invalida\"}"));

        assertDoesNotThrow(() -> gateway.enviar("+5511999990000", "oi", "r1"));
        servidor.verify();
    }

    @Test
    @DisplayName("Deve propagar o limite de taxa (429) para o consumer tentar de novo")
    void devePropagar429() {
        esperarToken("oat_um");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":true,\"code\":\"rate_limited\",\"message\":\"Tente em 42s\"}"));

        assertThrows(HttpClientErrorException.class, () -> gateway.enviar("+5511999990000", "oi", "r1"));
    }

    @Test
    @DisplayName("Deve propagar a falta de estoque no provedor (409) para o consumer tentar de novo")
    void devePropagar409() {
        esperarToken("oat_um");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single"))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":true,\"code\":\"provider_out_of_stock\",\"message\":\"sem estoque\"}"));

        assertThrows(HttpClientErrorException.class, () -> gateway.enviar("+5511999990000", "oi", "r1"));
    }

    @Test
    @DisplayName("Deve propagar o erro 5xx do provedor para o consumer tentar de novo")
    void devePropagar5xx() {
        esperarToken("oat_um");
        servidor.expect(requestTo(BASE + "/v1/sms/send/single")).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(HttpServerErrorException.class, () -> gateway.enviar("+5511999990000", "oi", "r1"));
    }

    @Test
    @DisplayName("Deve recusar subir sem a chave do SMSGo configurada")
    void deveExigirChave() {
        RestClient cliente = RestClient.builder().baseUrl(BASE).build();

        assertThrows(IllegalStateException.class, () -> new SmsGoGateway(cliente, ""));
        assertThrows(IllegalStateException.class, () -> new SmsGoGateway(cliente, "   "));
        assertThrows(IllegalStateException.class, () -> new SmsGoGateway(cliente, null));
    }

    private void esperarToken(String token) {
        servidor.expect(requestTo(BASE + "/v1/auth/token"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("SMSGo-key", "chave-permanente"))
                .andRespond(withSuccess("{\"token\":\"" + token + "\",\"mode\":\"live\"}", MediaType.APPLICATION_JSON));
    }
}
