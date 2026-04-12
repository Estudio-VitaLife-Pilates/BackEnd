package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.config.GerenciadorTokenJwt;
import com.pilates.thais.almeida.dto.usuario.UsuarioListarDto;
import com.pilates.thais.almeida.dto.usuario.UsuarioTokenDto;
import com.pilates.thais.almeida.entity.Usuario;
import com.pilates.thais.almeida.mapper.UsuarioMapper;
import com.pilates.thais.almeida.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private PasswordEncoder passwordEncoder;
    private UsuarioRepository usuarioRepository;
    private GerenciadorTokenJwt gerenciadorTokenJwt;
    private AuthenticationManager authenticationManager;

    public UsuarioService(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, GerenciadorTokenJwt gerenciadorTokenJwt, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.authenticationManager = authenticationManager;
    }

    public void criar(Usuario usuario){
        String senhaCrip = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCrip);

        usuarioRepository.save(usuario);
    }

    public UsuarioTokenDto autenticar(Usuario usuario){
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getSenha()
        );

        Authentication authentication = authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuario.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(404, "Usuário não encontrado", null)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = gerenciadorTokenJwt.generateToken(authentication);

        return UsuarioMapper.toTokenDto(usuarioAutenticado, token);
    }

    public List<UsuarioListarDto> listarTodos() {

        List<Usuario> usuariosEncontrados = usuarioRepository.findAll();
        return usuariosEncontrados.stream().map(UsuarioMapper::toListarDto).toList();

    }
}
