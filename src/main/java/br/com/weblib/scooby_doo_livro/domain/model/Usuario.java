package br.com.weblib.scooby_doo_livro.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String hashSenha;
}
