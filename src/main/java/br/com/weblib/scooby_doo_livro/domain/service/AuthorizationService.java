package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.domain.dtos.request.AuthenticationDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.request.RegisterDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.LoginResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.UserRole;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RegraDeNegocioException;
import br.com.weblib.scooby_doo_livro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    private final ApplicationContext context; // Truque sênior para pegar o AuthManager sem ciclo
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDTO login(AuthenticationDTO data) {
        // Recupera o Bean do AuthenticationManager em tempo de execução
        // Isso evita o erro comum de ciclo: SecurityConfig -> AuthManager -> Service -> SecurityConfig
        AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                data.email(),
                data.password()
        );

        // O Spring faz a mágica de verificar a senha aqui
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        // Gera o token
        Usuario usuarioAutenticado = (Usuario) auth.getPrincipal();
        String token = tokenService.generateToken(usuarioAutenticado);

        return new LoginResponseDTO(token, usuarioAutenticado.getId());
    }

    @Transactional
    public void register(RegisterDTO data) {
        // 1. Validação de Regra de Negócio (Unicidade)
        if (this.usuarioRepository.findByEmail(data.email()) != null) {
            throw new RegraDeNegocioException("Este e-mail já está cadastrado.");
        }

        // 2. Regra de Negócio (Definição de Role)
        UserRole role = UserRole.USER;
        if (data.email().endsWith("@unifor.br")) { // Melhor usar endsWith do que contains para segurança
            role = UserRole.ADMIN;
        }

        // 3. Criptografia
        String encryptedPassword = passwordEncoder.encode(data.password());

        // 4. Persistência
        Usuario newUser = new Usuario(
                data.nome(),
                data.email(),
                encryptedPassword,
                role
        );

        this.usuarioRepository.save(newUser);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username); // spring security já
        // é capaz de consultar os usuários no banco
    }
}
