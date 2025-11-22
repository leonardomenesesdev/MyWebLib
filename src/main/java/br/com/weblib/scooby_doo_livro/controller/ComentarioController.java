package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Comentario.ComentarioRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.ComentarioResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comentario")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ComentarioController {
    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> criar(@RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.ok(comentarioService.comentar(dto));
    }

    // Ex: GET /api/comentario/livro/40?page=0&size=10&sort=data,desc
    @GetMapping("/livro/{idLivro}")
    public ResponseEntity<Page<ComentarioResponseDTO>> listarPorLivro(
            @PathVariable Long idLivro,
            @PageableDefault(sort = "data", direction = Sort.Direction.DESC, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(comentarioService.listarPorLivro(idLivro, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        comentarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
