package br.com.weblib.scooby_doo_livro.domain.model.Usuario;

public record RegisterDTO(String nome, String email, String password,
                          UserRole role) {
}
