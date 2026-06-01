package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.config.GerenciadorTokenJwt;
import com.pilates.thais.almeida.dto.usuario.UsuarioListarDto;
import com.pilates.thais.almeida.dto.usuario.UsuarioTokenDto;
import com.pilates.thais.almeida.entity.Usuario;
import com.pilates.thais.almeida.exceptions.CadastroUsuarioConflitoEmail;
import com.pilates.thais.almeida.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() {

        // Given
        String email = "joao@gmail.com";
        String senha = "senha123";

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("João Silva");
        usuario.setEmail(email);
        usuario.setSenha("$2a$10$senhaCriptografada");

        Authentication authentication = Mockito.mock(Authentication.class);

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

        // When
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        Mockito.when(gerenciadorTokenJwt.generateToken(authentication)).thenReturn(token);

        // Then
        UsuarioTokenDto resultado = service.autenticar(usuario);

        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getToken());

        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).findByEmail(email);
        Mockito.verify(gerenciadorTokenJwt, Mockito.times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("Deve falhar no login com email inválido")
    void deveFalharNoLoginComEmailInvalido() {

        // Given
        String email = "usuario_inexistente@gmail.com";
        String senha = "senha123";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);

        // When
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.autenticar(usuario);
        });

        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
    }

    @Test
    @DisplayName("Deve falhar no login quando usuário não é encontrado no banco")
    void deveFalharNoLoginQuandoUsuarioNaoEEncontrado() {

        // Given
        String email = "usuario_inexistente@gmail.com";
        String senha = "senha123";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);

        Authentication authentication = Mockito.mock(Authentication.class);

        // When
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(ResponseStatusException.class, () -> {
            service.autenticar(usuario);
        });

        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve gerar token JWT após autenticação bem-sucedida")
    void deveGerarTokenJwtAposPosAutenticacao() {

        // Given
        String email = "joao@gmail.com";
        String senha = "senha123";

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("João Silva");
        usuario.setEmail(email);
        usuario.setSenha("$2a$10$senhaCriptografada");

        Authentication authentication = Mockito.mock(Authentication.class);

        String tokenEsperado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvQGdtYWlsLmNvbSJ9.asdfghjkl";

        // When
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        Mockito.when(gerenciadorTokenJwt.generateToken(authentication)).thenReturn(tokenEsperado);

        // Then
        UsuarioTokenDto resultado = service.autenticar(usuario);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(tokenEsperado, resultado.getToken());

        Mockito.verify(gerenciadorTokenJwt, Mockito.times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("Deve retornar dados do usuário junto com o token após login")
    void deveRetornarDadosDoUsuarioComToken() {

        // Given
        String email = "joao@gmail.com";
        String senha = "senha123";

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("João Silva");
        usuario.setEmail(email);
        usuario.setSenha("$2a$10$senhaCriptografada");

        Authentication authentication = Mockito.mock(Authentication.class);
        String token = "tokenJWT";

        // When
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        Mockito.when(gerenciadorTokenJwt.generateToken(authentication)).thenReturn(token);

        // Then
        UsuarioTokenDto resultado = service.autenticar(usuario);

        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getToken());
        Assertions.assertEquals(email, usuario.getEmail());
        Assertions.assertEquals("João Silva", usuario.getNome());

        Mockito.verify(repository, Mockito.times(1)).findByEmail(email);
    }

    // -------------------- POST /usuarios/logout --------------------

    @Test
    @DisplayName("Deve limpar o contexto de segurança ao fazer logout")
    void deveLimparContextoDeSegurancaAoFazerLogout() {

        // Given
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        service.logout();

        // Then
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // -------------------- POST /usuarios (criar) --------------------

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {

        // Given
        Usuario usuario = new Usuario();
        usuario.setNome("Maria Silva");
        usuario.setEmail("maria@gmail.com");
        usuario.setSenha("senha123");

        // When
        Mockito.when(repository.existsUsuarioByEmail(usuario.getEmail())).thenReturn(false);
        Mockito.when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$senhaCriptografada");

        service.criar(usuario);

        // Then
        Assertions.assertEquals("$2a$10$senhaCriptografada", usuario.getSenha());

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("senha123");
        Mockito.verify(repository, Mockito.times(1)).existsUsuarioByEmail(usuario.getEmail());
        Mockito.verify(repository, Mockito.times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email já cadastrado")
    void deveLancarExcecaoAoCriarUsuarioComEmailJaCadastrado() {

        // Given
        Usuario usuario = new Usuario();
        usuario.setNome("Maria Silva");
        usuario.setEmail("maria@gmail.com");
        usuario.setSenha("senha123");

        // When
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("$2a$10$senhaCriptografada");
        Mockito.when(repository.existsUsuarioByEmail(usuario.getEmail())).thenReturn(true);

        // Then
        Assertions.assertThrows(CadastroUsuarioConflitoEmail.class, () -> {
            service.criar(usuario);
        });

        Mockito.verify(repository, Mockito.times(1)).existsUsuarioByEmail(usuario.getEmail());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    // -------------------- GET /usuarios --------------------

    @Test
    @DisplayName("Deve retornar lista de usuários corretamente")
    void deveRetornarListaDeUsuarios() {

        // Given
        Usuario u1 = new Usuario();
        u1.setId(1);
        u1.setNome("João Silva");
        u1.setEmail("joao@gmail.com");

        Usuario u2 = new Usuario();
        u2.setId(2);
        u2.setNome("Maria Silva");
        u2.setEmail("maria@gmail.com");

        // When
        Mockito.when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<UsuarioListarDto> resultado = service.listarTodos();

        // Then
        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("João Silva", resultado.get(0).getNome());
        Assertions.assertEquals("Maria Silva", resultado.get(1).getNome());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver usuários")
    void deveRetornarListaVaziaDeUsuarios() {

        // When
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<UsuarioListarDto> resultado = service.listarTodos();

        // Then
        Assertions.assertTrue(resultado.isEmpty());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }
}

