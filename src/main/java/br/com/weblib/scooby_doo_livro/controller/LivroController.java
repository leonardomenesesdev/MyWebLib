package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import br.com.weblib.scooby_doo_livro.domain.service.LivroService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<Livro>> getAll(){
        return ResponseEntity.ok().body(livroService.listarLivros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> getById(@PathVariable Long id){
        return ResponseEntity.ok(livroService.getLivroById(id));
    }

    @GetMapping("/pesquisar/{termo}")
    public ResponseEntity<List<Livro>> getByAutorOrTitulo(@PathVariable String termo){
        return ResponseEntity.ok().body(livroService.getByAutorOrTitulo(termo));
    }
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Livro>> buscarPorTitulo(@PathVariable String titulo){
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo));
    }


    @GetMapping("/buscar/{autor}")
    public ResponseEntity<List<Livro>> buscarPorAutor(@PathVariable String autor){
        return ResponseEntity.ok(livroService.getByAutor(autor));
    }

    @GetMapping("/filtrar/{categoria}")
    public ResponseEntity<List<Livro>> buscarPorCategoria(@PathVariable EnumCategoria categoria) {
        List<Livro> livros = livroService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(livros);
    }

    @PostMapping()
    public ResponseEntity<Livro> create(@RequestBody Livro livro) {
        Livro novoLivro = livroService.cadastrar(livro);
        return ResponseEntity.ok(novoLivro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> update(@PathVariable Long id, @RequestBody Livro livroAtualizado) throws AuthenticationException {
        Livro livro = livroService.atualizar(id, livroAtualizado);
        return ResponseEntity.ok(livro);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        livroService.delete(id);
    }



}
