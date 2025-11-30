package br.com.weblib.scooby_doo_livro.controller;


import br.com.weblib.scooby_doo_livro.domain.dtos.response.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.request.LivroRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import br.com.weblib.scooby_doo_livro.domain.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/livro")
public class LivroController {

    private final LivroService livroService;

    // GET /api/livro (Paginado) -> Retorna Resumo
    @GetMapping
    public ResponseEntity<Page<LivroResumoDTO>> getAll(
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    ){
        return ResponseEntity.ok(livroService.listarLivros(pageable));
    }

    // GET /api/livro/{id} -> Retorna Detalhes Completos
    @GetMapping("/{id}")
    public ResponseEntity<LivroDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    // GET /api/livro/pesquisar/{termo} -> Busca Geral
    @GetMapping("/pesquisar/{termo}")
    public ResponseEntity<List<LivroResumoDTO>> getByAutorOrTitulo(@PathVariable String termo){
        return ResponseEntity.ok(livroService.buscarPorAutorOuTitulo(termo));
    }

    // GET /api/livro/titulo/{titulo}
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<LivroResumoDTO>> buscarPorTitulo(@PathVariable String titulo){
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo));
    }

    // GET /api/livro/buscar/{autor}
    @GetMapping("/buscar/{autor}")
    public ResponseEntity<List<LivroResumoDTO>> buscarPorAutor(@PathVariable String autor){
        return ResponseEntity.ok(livroService.buscarPorAutor(autor));
    }

    // GET /api/livro/filtrar/{categoria}
    @GetMapping("/filtrar/{categoria}")
    public ResponseEntity<List<LivroResumoDTO>> buscarPorCategoria(@PathVariable EnumCategoria categoria) {
        return ResponseEntity.ok(livroService.buscarPorCategoria(categoria));
    }

    // --- ESCRITA (CRUD) ---

    // POST /api/livro
    @PostMapping
    public ResponseEntity<LivroDTO> create(@RequestBody @Valid LivroRequestDTO dados) {
        LivroDTO novoLivro = livroService.cadastrar(dados);
        // Retorna 201 Created e o objeto criado (importante para o front pegar o ID)
        return ResponseEntity.status(HttpStatus.CREATED).body(novoLivro);
    }

    // PUT /api/livro/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LivroDTO> update(@PathVariable Long id,
                                           @RequestBody @Valid LivroRequestDTO dados) {
        LivroDTO livroAtualizado = livroService.atualizar(id, dados);
        return ResponseEntity.ok(livroAtualizado);
    }

    // DELETE /api/livro/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        livroService.delete(id);
        // Retorna 204 No Content (Padrão para deleção com sucesso)
        return ResponseEntity.noContent().build();
    }
}