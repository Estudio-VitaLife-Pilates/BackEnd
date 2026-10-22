package com.pilates.thais.almeida.notificacao;

import java.util.Optional;
import java.util.regex.Pattern;

public final class TelefoneBrasil {

    private static final Pattern CELULAR_NACIONAL = Pattern.compile("^[1-9][1-9]9\\d{8}$");

    private TelefoneBrasil() {
    }

    public static Optional<String> paraInternacional(String telefone) {
        if (telefone == null) {
            return Optional.empty();
        }

        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.startsWith("00")) {
            digitos = digitos.substring(2);
        }

        String nacional;
        if (digitos.length() == 13 && digitos.startsWith("55")) {
            nacional = digitos.substring(2);
        } else if (digitos.length() == 11) {
            nacional = digitos;
        } else {
            return Optional.empty();
        }

        return CELULAR_NACIONAL.matcher(nacional).matches()
                ? Optional.of("+55" + nacional)
                : Optional.empty();
    }
}
