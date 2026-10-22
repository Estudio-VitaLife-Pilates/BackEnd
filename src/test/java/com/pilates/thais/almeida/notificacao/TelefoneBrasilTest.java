package com.pilates.thais.almeida.notificacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TelefoneBrasilTest {

    @ParameterizedTest(name = "\"{0}\" vira {1}")
    @DisplayName("Deve converter celulares brasileiros para o formato internacional")
    @CsvSource({
            "11999990000,       +5511999990000",
            "(11) 99999-0000,   +5511999990000",
            "+55 11 99999-0000, +5511999990000",
            "5511999990000,     +5511999990000",
            "0055 11 99999-0000,+5511999990000",
            "  11 99999 0000  , +5511999990000",
            "55999990000,       +5555999990000"
    })
    void deveConverterCelulares(String entrada, String esperado) {
        assertEquals(Optional.of(esperado), TelefoneBrasil.paraInternacional(entrada));
    }

    @ParameterizedTest(name = "\"{0}\" e recusado")
    @DisplayName("Deve recusar numeros que nao sao celular valido")
    @NullAndEmptySource
    @ValueSource(strings = {
            "   ",
            "abc",
            "1133334444",
            "11 3333-4444",
            "1199990000",
            "119999900001",
            "0999990000",
            "11099990000",
            "551133334444",
            "+1 415 555 0100"
    })
    void deveRecusarNumerosInvalidos(String entrada) {
        assertEquals(Optional.empty(), TelefoneBrasil.paraInternacional(entrada));
    }

    @Test
    @DisplayName("55 no inicio de um numero nacional de 11 digitos e o DDD do RS, nao o codigo do pais")
    void deveTratar55ComoDddQuandoNumeroNacional() {
        assertEquals(Optional.of("+5555999990000"), TelefoneBrasil.paraInternacional("55999990000"));
    }
}
