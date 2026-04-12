package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.usuario.UsuarioCriacaoDto;
import com.pilates.thais.almeida.dto.usuario.UsuarioLoginDto;
import com.pilates.thais.almeida.dto.usuario.UsuarioSessaoDto;
import com.pilates.thais.almeida.dto.usuario.UsuarioTokenDto;
import com.pilates.thais.almeida.entity.Usuario;
import com.pilates.thais.almeida.mapper.UsuarioMapper;
import com.pilates.thais.almeida.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    public static final String COOKIE_NOME = "authToken";

    @Value("${jwt.validity}")
    private long jwtValidity;

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> criar(
            @RequestBody @Valid UsuarioCriacaoDto usuarioCriacaoDto
    ){
        Usuario novoUsuario = UsuarioMapper.toEntity(usuarioCriacaoDto);
        usuarioService.criar(novoUsuario);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioSessaoDto> login(
            @RequestBody UsuarioLoginDto usuarioLoginDto,
            HttpServletResponse response
    ){
        Usuario usuario = UsuarioMapper.toEntity(usuarioLoginDto);

        UsuarioTokenDto autenticado = usuarioService.autenticar(usuario);

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NOME, autenticado.getToken())
                .httpOnly(true)                          // inacessível ao JavaScript
                .secure(false)                           // true em produção (exige HTTPS)
                .sameSite("Strict")                      // bloqueia envio cross-site (mitiga CSRF)
                .path("/")                               // valido para toda a aplicacao
                .maxAge(Duration.ofSeconds(jwtValidity)) // expira junto com o token JWT
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        UsuarioSessaoDto sessao = UsuarioMapper.toSessao(autenticado);
        return ResponseEntity.ok(sessao);
    }
}
