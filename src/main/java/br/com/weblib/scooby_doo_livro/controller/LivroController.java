package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
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

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Livro>> buscarPorTitulo(@PathVariable String titulo){
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo));
    }

    @GetMapping("/buscar/{autor}")
    public ResponseEntity<List<Livro>> buscarPorAutor(@PathVariable String autor){
        return ResponseEntity.ok(livroService.getByAutor(autor));
    }

}
