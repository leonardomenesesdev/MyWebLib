package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.dtos.request.AuthenticationDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.request.RegisterDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.LoginResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.service.AuthorizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        LoginResponseDTO tokenResponse = authorizationService.login(data);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO data) {
        authorizationService.register(data);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}