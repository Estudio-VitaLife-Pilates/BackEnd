package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.usuario.*;
import com.pilates.thais.almeida.entity.Usuario;

public class UsuarioMapper {
    public static Usuario toEntity(UsuarioCriacaoDto usuarioCriacaoDto){
        Usuario usuario = new Usuario();

        usuario.setNome(usuarioCriacaoDto.getNome());
        usuario.setEmail(usuarioCriacaoDto.getEmail());
        usuario.setSenha(usuarioCriacaoDto.getSenha());

        return usuario;
    }

    public static Usuario toEntity(UsuarioLoginDto usuarioLoginDto){
        Usuario usuario = new Usuario();

        usuario.setEmail(usuarioLoginDto.getEmail());
        usuario.setSenha(usuarioLoginDto.getSenha());

        return usuario;
    }

    public static UsuarioTokenDto toTokenDto(Usuario usuario, String token){
        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();

        usuarioTokenDto.setUserId(usuario.getId());
        usuarioTokenDto.setEmail(usuario.getEmail());
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setToken(token);

        return usuarioTokenDto;
    }

    public static UsuarioSessaoDto toSessao(UsuarioTokenDto tokenDto){
        UsuarioSessaoDto dto = new UsuarioSessaoDto();

        dto.setUserId(tokenDto.getUserId());
        dto.setEmail(tokenDto.getEmail());
        dto.setNome(tokenDto.getNome());

        return dto;
    }

    public static UsuarioListarDto toListarDto(Usuario usuario){
        UsuarioListarDto usuarioListarDto = new UsuarioListarDto();

        usuarioListarDto.setId(usuario.getId());
        usuarioListarDto.setEmail(usuario.getEmail());
        usuarioListarDto.setNome(usuario.getNome());

        return usuarioListarDto;
    }
}
