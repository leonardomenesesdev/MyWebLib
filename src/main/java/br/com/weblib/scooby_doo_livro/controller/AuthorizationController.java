package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.*;
import br.com.weblib.scooby_doo_livro.domain.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthorizationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken emailPassword =
                new UsernamePasswordAuthenticationToken(data.email(),
                        data.password());
        Authentication auth = this.authenticationManager.authenticate(emailPassword);

        String token = tokenService.generateToken((Usuario) auth.getPrincipal());
        Usuario usuarioAutenticado = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(new LoginResponseDTO(token, usuarioAutenticado.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (this.usuarioRepository.findByEmail(data.email()) != null) {
            // caso ja exista alguem no banco com esse email
            return ResponseEntity.badRequest().build();
        }

        UserRole userRole = UserRole.USER;

        if (data.email().contains("@unifor.br")) {
            userRole = UserRole.ADMIN;
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        Usuario usuario = new Usuario(data.nome(), data.email(),
                encryptedPassword,
                userRole);

        this.usuarioRepository.save(usuario);
        return ResponseEntity.ok().body("Usuário registrado com sucesso!");
    }
}
