package com.pilates.thais.almeida.config;

import com.pilates.thais.almeida.controller.UsuarioController;
import com.pilates.thais.almeida.service.AutenticacaoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AutenticacaoFilterTest {

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private GerenciadorTokenJwt jwtTokenManager;

    @InjectMocks
    private AutenticacaoFilter filtro;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private UserDetails criarUserDetails(String email) {
        return new User(email, "senha-criptografada", List.<GrantedAuthority>of());
    }

    // -------------------- Cenário feliz --------------------

    @Test
    @DisplayName("Deve registrar autenticação no contexto quando o token do cookie é válido")
    void deveAutenticarComTokenValido() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(UsuarioController.COOKIE_NOME, "token-valido"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        UserDetails userDetails = criarUserDetails("ana@gmail.com");

        // When
        Mockito.when(jwtTokenManager.getUsernameFromToken("token-valido")).thenReturn("ana@gmail.com");
        Mockito.when(autenticacaoService.loadUserByUsername("ana@gmail.com")).thenReturn(userDetails);
        Mockito.when(jwtTokenManager.validateToken("token-valido", userDetails)).thenReturn(true);

        filtro.doFilter(request, response, filterChain);

        // Then
        Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        Assertions.assertEquals("ana@gmail.com", SecurityContextHolder.getContext().getAuthentication().getName());
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
    }

    // -------------------- Token órfão (usuário apagado depois de emitido) --------------------

    @Test
    @DisplayName("Não deve derrubar a requisição quando o token é válido mas o usuário não existe mais")
    void naoDeveQuebrarComTokenDeUsuarioApagado() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(UsuarioController.COOKIE_NOME, "token-orfao"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // When
        Mockito.when(jwtTokenManager.getUsernameFromToken("token-orfao")).thenReturn("fantasma@gmail.com");
        Mockito.when(autenticacaoService.loadUserByUsername("fantasma@gmail.com"))
                .thenThrow(new UsernameNotFoundException("usuário: fantasma@gmail.com não encontrado"));

        // Then — não pode lançar exceção, a requisição deve seguir anônima
        Assertions.assertDoesNotThrow(() -> filtro.doFilter(request, response, filterChain));

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
    }

    // -------------------- Sem token --------------------

    @Test
    @DisplayName("Deve seguir a cadeia de filtros sem autenticar quando não há token")
    void deveSeguirSemAutenticarQuandoNaoHaToken() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // When
        filtro.doFilter(request, response, filterChain);

        // Then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
        Mockito.verify(autenticacaoService, Mockito.never()).loadUserByUsername(Mockito.any());
    }

    // -------------------- Token expirado --------------------

    @Test
    @DisplayName("Deve seguir a cadeia de filtros sem autenticar quando o token está expirado")
    void deveSeguirSemAutenticarQuandoTokenExpirado() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(UsuarioController.COOKIE_NOME, "token-expirado"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // When
        Claims claimsVazias = Mockito.mock(Claims.class);
        Mockito.when(claimsVazias.getSubject()).thenReturn("expirado@gmail.com");
        Mockito.when(jwtTokenManager.getUsernameFromToken("token-expirado"))
                .thenThrow(new ExpiredJwtException(null, claimsVazias, "token expirado"));

        filtro.doFilter(request, response, filterChain);

        // Then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
        Mockito.verify(autenticacaoService, Mockito.never()).loadUserByUsername(Mockito.any());
    }
}
