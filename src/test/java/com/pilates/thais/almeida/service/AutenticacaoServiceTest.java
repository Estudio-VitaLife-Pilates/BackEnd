package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.usuario.UsuarioDetalhesDto;
import com.pilates.thais.almeida.entity.Usuario;
import com.pilates.thais.almeida.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    void deveRetornarUsuarioDetalhesQuandoUsuarioExiste() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@exemplo.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@exemplo.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails = autenticacaoService.loadUserByUsername("teste@exemplo.com");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UsuarioDetalhesDto);
        assertEquals("teste@exemplo.com", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail("naoexiste@exemplo.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                autenticacaoService.loadUserByUsername("naoexiste@exemplo.com"));
    }
}
