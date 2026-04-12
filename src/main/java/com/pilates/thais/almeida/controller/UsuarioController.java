package com.pilates.thais.almeida.controller;

import com.pilates.thais.almeida.dto.usuario.UsuarioCriacaoDto;
import com.pilates.thais.almeida.entity.Usuario;
import com.pilates.thais.almeida.mapper.UsuarioMapper;
import com.pilates.thais.almeida.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
