package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao.AvaliacaoResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.service.AvaliacaoService;
import br.com.weblib.scooby_doo_livro.domain.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/avaliacao")
@CrossOrigin("*")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private LivroService livroService;

    @PostMapping("/avaliar/{livroId}/{nota}/{usuarioId}")
    public ResponseEntity<AvaliacaoResponseDTO> avaliar(@PathVariable Long livroId,
                                                        @PathVariable Integer nota,
                                                        @PathVariable Long usuarioId) {
        Avaliacao avaliacao = avaliacaoService.avaliarLivro(livroId, usuarioId,
                nota);
        return ResponseEntity.ok(new AvaliacaoResponseDTO(avaliacao.getId(),
                livroId, avaliacao.getUsuario().getEmail(), avaliacao.getNota()));
    }

    @DeleteMapping("/avaliar/cancelar/{livroId}/{usuarioId}")
    public ResponseEntity cancelarAvaliacao(@PathVariable Long livroId,
                                            @PathVariable Long usuarioId) {
        avaliacaoService.removerAvaliacao(livroId, usuarioId);
        return ResponseEntity.ok("Avaliação removida com sucesso.");
    }
}
