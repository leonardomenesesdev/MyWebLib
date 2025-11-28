package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.AtualizarStatusDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.service.StatusLeituraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LivroStatusController {
    private final StatusLeituraService statusLeituraService;

    @PutMapping
    public ResponseEntity<AtualizarStatusDTO>atualizarStatus(@RequestBody AtualizarStatusDTO dto, Authentication authentication) {
        System.out.println("Controller chamado");
        Usuario usuario = (Usuario) authentication.getPrincipal();
        statusLeituraService.atualizarStatus(usuario.getId(), dto);
        return  ResponseEntity.ok().body(dto);
    }

    @GetMapping("/livro/{idLivro}")
    public ResponseEntity<String> obterStatusLeitura(@PathVariable Long idLivro, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String status = statusLeituraService.getStatusAtual(usuario.getId(), idLivro);
        return ResponseEntity.ok(status);
    }
    @GetMapping("/usuario/{status}")
    public ResponseEntity<List<LivroDTO>> listarMeusLivrosPorStatus(
            @PathVariable EnumStatusLeitura status,
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        List<LivroDTO> livros = statusLeituraService.listarLivrosPorStatus(usuarioLogado.getId(), status);

        return ResponseEntity.ok(livros);
    }

    //controller pra pegar os livros de outro usuário
    @GetMapping("/usuario/{userId}/{status}")
    public ResponseEntity<List<LivroDTO>> listarLivrosDeOutroUsuario(
            @PathVariable Long userId,
            @PathVariable EnumStatusLeitura status
    ) {
        List<LivroDTO> livros = statusLeituraService.listarLivrosPorStatus(userId, status);

        return ResponseEntity.ok(livros);
    }
}
