package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import br.com.weblib.scooby_doo_livro.domain.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.security.sasl.AuthenticationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/livro")
public class LivroController {
    private final LivroService livroService;

    @GetMapping
    public ResponseEntity<Page<LivroDTO>> getAll(
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    ){
        return ResponseEntity.ok(livroService.listarLivros(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<LivroDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(livroService.getLivroDTOById(id));
    }

    @GetMapping("/pesquisar/{termo}")
    public ResponseEntity<List<LivroDTO>> getByAutorOrTitulo(@PathVariable String termo){
        return ResponseEntity.ok().body(livroService.getByAutorOrTitulo(termo));
    }
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Livro>> buscarPorTitulo(@PathVariable String titulo){
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo));
    }


    //não ta sendo usada pra nada no front. removemos?
    @GetMapping("/buscar/{autor}")
    public ResponseEntity<List<Livro>> buscarPorAutor(@PathVariable String autor){
        return ResponseEntity.ok(livroService.getByAutor(autor));
    }

    @GetMapping("/filtrar/{categoria}")
    public ResponseEntity<List<Livro>> buscarPorCategoria(@PathVariable EnumCategoria categoria) {
        List<Livro> livros = livroService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(livros);
    }

    // CRUD
    @PostMapping()
    public ResponseEntity create(@RequestBody LivroRequestDTO livro) {
        LivroRequestDTO novoLivro = livroService.cadastrar(livro);
        return ResponseEntity.ok().body("Livro registrado com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id,
                                        @RequestBody LivroRequestDTO livroAtualizado) throws AuthenticationException {
        LivroRequestDTO livro = livroService.atualizar(id, livroAtualizado);
        return ResponseEntity.ok().body("Livro atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        livroService.delete(id);
    }


}
