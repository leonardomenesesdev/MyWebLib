package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.AuthenticationDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.RegisterDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(data.email(),
                        data.password());

        Authentication auth = this.authenticationManager.authenticate(token);

        return ResponseEntity.ok().body("Usuário logado com sucesso!");
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (this.usuarioRepository.findByEmail(data.email()) != null) {
            // caso ja exista alguem no banco com esse email
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        Usuario usuario = new Usuario(data.nome(), data.email(),
                encryptedPassword,
                data.role());

        this.usuarioRepository.save(usuario);

        return ResponseEntity.ok().body("Usuário registrado com sucesso!");
    }
}
