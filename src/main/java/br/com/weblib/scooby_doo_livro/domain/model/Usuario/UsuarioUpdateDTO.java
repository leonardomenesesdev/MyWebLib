package br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
        @Size(min = 2, message = "Nome deve ter pelo menos 2 caracteres")
        String nome,
        @Email(message = "Formato de e-mail inválido")
        String email) {}
