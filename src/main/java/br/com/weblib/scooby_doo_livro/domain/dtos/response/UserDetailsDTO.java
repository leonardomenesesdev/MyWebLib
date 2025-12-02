package br.com.weblib.scooby_doo_livro.domain.dtos.response;

import br.com.weblib.scooby_doo_livro.domain.model.enums.UserRole;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import lombok.RequiredArgsConstructor;


public record UserDetailsDTO(Long id, String email, String nome, UserRole role) {
    public UserDetailsDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }
}
