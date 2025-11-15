package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.service.BookPopulatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/livros")
@RequiredArgsConstructor
public class BookPopulatorController {

    private final BookPopulatorService bookPopulatorService;

    /**
     * Endpoint para popular o banco de dados com livros da Google Books API
     *
     * Uso: POST http://localhost:8080/api/admin/livros/popular?quantidade=50
     */
    @PostMapping("/popular")
    public ResponseEntity<String> popularBanco(
            @RequestParam(defaultValue = "50") int quantidade) {

        try {
            bookPopulatorService.popularBancoDeDados(quantidade);
            return ResponseEntity.ok(
                    String.format("População iniciada! Importando até %d livros.", quantidade)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao popular banco: " + e.getMessage());
        }
    }
}