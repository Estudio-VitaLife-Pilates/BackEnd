package com.pilates.thais.almeida.config;

import com.pilates.thais.almeida.dto.usuario.UsuarioDetalhesDto;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        // 1. Coleta os dados de autenticação da Thread atual do Spring Security (Injetados pelo seu filtro JWT)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Se não houver ninguém logado ou for um acesso anônimo, retorna vazio
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        // 3. Pega o "Principal" da sessão
        Object principal = authentication.getPrincipal();

        // 4. Se o principal for o seu DTO (o que significa que o JWT é válido), extrai o ID dele
        if (principal instanceof UsuarioDetalhesDto usuarioLogado) {
            return Optional.of(usuarioLogado.getId());
        }

        return Optional.empty();
    }
}