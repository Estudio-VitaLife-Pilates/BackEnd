package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.usuario.UsuarioCriacaoDto;
import com.pilates.thais.almeida.entity.Usuario;

public class UsuarioMapper {
    public static Usuario toEntity(UsuarioCriacaoDto usuarioCriacaoDto){
        Usuario usuario = new Usuario();

        usuario.setNome(usuarioCriacaoDto.getNome());
        usuario.setEmail(usuarioCriacaoDto.getEmail());
        usuario.setSenha(usuarioCriacaoDto.getSenha());

        return usuario;
    }
}
