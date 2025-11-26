package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.service.LivroFavoritadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/livro/favoritos")
@CrossOrigin("*")
public class LivroFavoritadoController {


    private final LivroFavoritadoService favoritoService;

    public LivroFavoritadoController(LivroFavoritadoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    // POST /api/favoritos/toggle/{idLivro}
    // Funciona como um interruptor: se não tem, cria. Se tem, apaga.
    @PostMapping("/toggle/{idLivro}")
    public ResponseEntity<?> toggleFavorito(@PathVariable Long idLivro, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        try {
            favoritoService.alternarFavorito(usuario.getId(), idLivro);
            // Sucesso: Retorna 200 OK sem corpo
            return ResponseEntity.ok().build();

        } catch (ResponseStatusException e) {
            // ERRO DE REGRA DE NEGÓCIO:
            // Capturamos a exceção do Service (ex: "Livros na lista 'Quero Ler' não podem...")
            // E forçamos o retorno de um JSON com a chave "message".
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<LivroResumoDTO>> listarFavoritos(@PathVariable Long idUsuario) {
        System.out.println("usuario chamado");
        List<LivroResumoDTO> favoritos = favoritoService.listarFavoritosDoUsuario(idUsuario);
        return ResponseEntity.ok(favoritos);
    }
    // GET /api/favoritos/status/{idLivro}
    // Retorna true/false para o botão ficar vermelho ou cinza
    @GetMapping("/status/{idLivro}")
    public ResponseEntity<Boolean> verificarStatusFavorito(@PathVariable Long idLivro, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        boolean isFavorito = favoritoService.isFavorito(usuario.getId(), idLivro);

        return ResponseEntity.ok(isFavorito);
    }
}
