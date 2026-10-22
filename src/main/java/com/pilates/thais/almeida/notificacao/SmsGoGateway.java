package com.pilates.thais.almeida.notificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;


@Component
@ConditionalOnProperty(name = "lembrete.canal", havingValue = "sms")
public class SmsGoGateway implements SmsGateway {

    private static final Logger log = LoggerFactory.getLogger(SmsGoGateway.class);

    private static final Duration VALIDADE_TOKEN = Duration.ofHours(47);
    private static final int LIMITE_LOG_ERRO = 300;

    private final RestClient restClient;
    private final String apiKey;

    private String token;
    private Instant tokenValidoAte = Instant.MIN;

    @Autowired
    public SmsGoGateway(@Value("${sms.smsgo.base-url}") String baseUrl,
                        @Value("${sms.smsgo.api-key:}") String apiKey) {
        this(RestClient.builder().baseUrl(baseUrl).requestFactory(fabricaComTimeout()).build(), apiKey);
    }

    SmsGoGateway(RestClient restClient, String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("SMSGO_API_KEY nao configurada (obrigatoria com lembrete.canal=sms)");
        }
        this.restClient = restClient;
        this.apiKey = apiKey.trim();
    }

    private static JdkClientHttpRequestFactory fabricaComTimeout() {
        HttpClient cliente = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(cliente);
        fabrica.setReadTimeout(Duration.ofSeconds(10));
        return fabrica;
    }

    @Override
    public void enviar(String telefone, String texto, String referencia) {
        for (int tentativa = 1; ; tentativa++) {
            try {
                Map<String, Object> resposta = postarSms(telefone, texto, referencia);
                registrarSucesso(resposta, referencia);
                return;
            } catch (HttpClientErrorException e) {
                int status = e.getStatusCode().value();
                if (status == 401 && tentativa == 1) {
                    log.warn("SMSGo recusou o token (401); renovando e tentando mais uma vez");
                    invalidarToken();
                    continue;
                }
                if (status == 429 || status == 409) {
                
                    throw e;
                }
            
                log.error("SMSGo recusou o SMS ({}) [{}]: {}", status, referencia, resumir(e.getResponseBodyAsString()));
                return;
            }
        
        }
    }

    private Map<String, Object> postarSms(String telefone, String texto, String referencia) {
        return restClient.post()
                .uri("/v1/sms/send/single")
                .header("Authorization", "Bearer " + tokenValido())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("phone", telefone, "message", texto, "reference", referencia))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    private synchronized String tokenValido() {
        if (token == null || Instant.now().isAfter(tokenValidoAte)) {
            Map<String, Object> resposta = restClient.get()
                    .uri("/v1/auth/token")
                    .header("SMSGo-key", apiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            Object novoToken = resposta == null ? null : resposta.get("token");
            if (novoToken == null) {
                throw new IllegalStateException("SMSGo nao devolveu token em /v1/auth/token");
            }
            token = novoToken.toString();
            tokenValidoAte = Instant.now().plus(VALIDADE_TOKEN);
            log.info("Token do SMSGo obtido (modo {})", resposta.get("mode"));
        }
        return token;
    }

    private synchronized void invalidarToken() {
        token = null;
        tokenValidoAte = Instant.MIN;
    }

    private void registrarSucesso(Map<String, Object> resposta, String referencia) {
        Object id = resposta == null ? null : resposta.get("id");
        Object status = resposta == null ? null : resposta.get("status");
        boolean sandbox = resposta != null && Boolean.TRUE.equals(resposta.get("test"));
        log.info("SMSGo aceitou o SMS [{}]: id={}, status={}{}", referencia, id, status,
                sandbox ? " (MODO TESTE: nada foi despachado nem cobrado)" : "");
    }

    private String resumir(String corpo) {
        if (corpo == null) {
            return "";
        }
        return corpo.length() > LIMITE_LOG_ERRO ? corpo.substring(0, LIMITE_LOG_ERRO) + "..." : corpo;
    }
}
