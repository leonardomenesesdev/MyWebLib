package br.com.weblib.scooby_doo_livro.domain.model.Livro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {
    private String nome;
    private String email;
    private String senha; // ← Recebe "senha" do JSON

}